package org.jasig.cas.client.session;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;
import java.util.zip.Inflater;

/**
 * 基于 {@link org.jasig.cas.client.session.SingleSignOutHandler} 进行扩展
 * 可以自定义会话创建和销毁
 * 可以自定义过滤器之前和之后执行
 */
public abstract class CustomBaseClientFilter extends AbstractConfigurationFilter {

    private final static int DECOMPRESSION_FACTOR = 10;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** Mapping of token IDs and session IDs to HTTP sessions */
//    private SessionMappingStorage sessionMappingStorage = SingleSignOutFilter.getSingleSignOutHandler().getSessionMappingStorage();
    protected final Map<String, String> SESSION_CACHE = Collections.synchronizedMap(new HashMap<String, String>());

    /** The name of the artifact parameter.  This is used to capture the session identifier. */
    private String artifactParameterName = Protocol.CAS2.getArtifactParameterName();

    /** Parameter name that stores logout request for SLO */
    private String logoutParameterName = ConfigurationKeys.LOGOUT_PARAMETER_NAME.getDefaultValue();

    private List<String> safeParameters;

    private boolean eagerlyCreateSessions = true;

    /** The logout callback path configured at the CAS server, if there is one */
    private String logoutCallbackPath;

    /**
     * @param logoutCallbackPath The logout callback path configured at the CAS server.
     */
    public void setLogoutCallbackPath(String logoutCallbackPath) {
        this.logoutCallbackPath = logoutCallbackPath;
    }

    public void setEagerlyCreateSessions(final boolean eagerlyCreateSessions) {
        this.eagerlyCreateSessions = eagerlyCreateSessions;
    }

    /**
     * Initializes the component for use.
     */
    public synchronized void init() {
        if (this.safeParameters == null) {
            this.safeParameters = Arrays.asList(this.logoutParameterName, this.artifactParameterName);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        init();
    }

    /**
     * Determines whether the given request contains an authentication token.
     *
     * @param request HTTP reqest.
     *
     * @return True if request contains authentication token, false otherwise.
     */
    private boolean isTokenRequest(final HttpServletRequest request) {
        return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.artifactParameterName,
                this.safeParameters));
    }

    /**
     * Determines whether the given request is a CAS  logout request.
     *
     * @param request HTTP request.
     *
     * @return True if request is logout request, false otherwise.
     */
    private boolean isLogoutRequest(final HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return !isMultipartRequest(request)
                    && pathEligibleForLogout(request)
                    && CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName,
                    this.safeParameters));
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters));
        }
        return false;
    }

    private boolean isMultipartRequest(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart");
    }

    private boolean pathEligibleForLogout(HttpServletRequest request) {
        return logoutCallbackPath == null || logoutCallbackPath.equals(getPath(request));
    }

    /**
     * Returns the string as-is, unless it's <code>null</code>;
     * in this case an empty string is returned.
     *
     * @param string a possibly <code>null</code> string
     * @return a non-<code>null</code> string
     */
    private static String nullToEmpty(String string) {
        return string == null ? "" : string;
    }

    private String getPath(HttpServletRequest request) {
        return request.getServletPath() + nullToEmpty(request.getPathInfo());
    }

    /**
     * Associates a token request with the current HTTP session by recording the mapping
     * in the the configured {@link SessionMappingStorage} container.
     *
     * @param request HTTP request containing an authentication token.
     */
    private void recordSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(this.eagerlyCreateSessions);

        if (session == null) {
            logger.debug("No session currently exists (and none created).  Cannot record session information for single sign out.");
            return;
        }

        final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
        logger.debug("Recording session for token {}", token);

        if (CommonUtils.isNotBlank(token)) {
            customRecordSession(token, session);
        }
    }

    /**
     * 自定义记录会话
     * @param token
     * @param session
     */
    protected abstract void customRecordSession(final String token, final HttpSession session);

    /**
     * Uncompress a logout message (base64 + deflate).
     *
     * @param originalMessage the original logout message.
     * @return the uncompressed logout message.
     */
    private String uncompressLogoutMessage(final String originalMessage) {
        final byte[] binaryMessage = DatatypeConverter.parseBase64Binary(originalMessage);

        Inflater decompresser = null;
        try {
            // decompress the bytes
            decompresser = new Inflater();
            decompresser.setInput(binaryMessage);
            final byte[] result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];

            final int resultLength = decompresser.inflate(result);

            // decode the bytes into a String
            return new String(result, 0, resultLength, "UTF-8");
        } catch (final Exception e) {
            logger.error("Unable to decompress logout message", e);
            throw new RuntimeException(e);
        } finally {
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }

    /**
     * Destroys the current HTTP session for the given CAS logout request.
     *
     * @param request HTTP request containing a CAS logout message.
     */
    private void destroySession(final HttpServletRequest request) {
        String logoutMessage = CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters);
        if (CommonUtils.isBlank(logoutMessage)) {
            logger.error("Could not locate logout message of the request from {}", this.logoutParameterName);
            return;
        }

        if (!logoutMessage.contains("SessionIndex")) {
            logoutMessage = uncompressLogoutMessage(logoutMessage);
        }

        logger.trace("Logout request:\n{}", logoutMessage);
        final String token = XmlUtils.getTextForElement(logoutMessage, "SessionIndex");
        if (CommonUtils.isNotBlank(token)) {
//            final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);
//
//            if (session != null) {
//                customDestroySession(token, session);
//            }
            customDestroySession(token);
        }
    }

    /**
     * 自定义销毁会话
     * @param token
     * @param session
     */
//    protected abstract void customDestroySession(final String token, final HttpSession session);

    /**
     * 自定义销毁会话
     * @param token
     */
    protected abstract void customDestroySession(final String token);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (isTokenRequest(request)) {
            recordSession(request);
            logger.debug("custom `SingleSignOutHandler` record session success.");
        }

        if (isLogoutRequest(request)) {
            destroySession(request);
            logger.debug("custom `SingleSignOutHandler` destroy session success.");
        }

        customBeforeDo(servletRequest, servletResponse);

        filterChain.doFilter(servletRequest, servletResponse);

        customAfterDo(servletRequest, servletResponse);
    }

    /**
     * 自定义过滤器之前执行
     */
    protected void customBeforeDo(final ServletRequest servletRequest, final ServletResponse servletResponse) {};

    /**
     * 自定义过滤器之后执行
     */
    protected void customAfterDo(final ServletRequest servletRequest, final ServletResponse servletResponse) {};

    @Override
    public void destroy() {

    }

}
