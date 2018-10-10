package com.hnds.client.rest;

import com.hnds.client.session.GlobalRestTgtManager;
import com.hnds.client.utils.CustomCommonUtils;
import com.hnds.client.utils.RestAuthUtils;
import com.hnds.client.utils.SpringWebUtils;
import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.authentication.DefaultAuthenticationRedirectStrategy;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 13:11
 */
public class DefaultRestValidationFilter extends AbstractConfigurationFilter {

    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();

    private String clientRestLoginUrl;
    private GlobalRestTgtManager globalRestTgtManager;
    private RestTemplate restTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        this.init();
    }

    public void init() {
        CommonUtils.assertTrue(CommonUtils.isNotBlank(clientRestLoginUrl), "clientRestLoginUrl must be set.");
        CommonUtils.assertTrue(globalRestTgtManager != null, "globalRestTgtManager must be set.");
        CommonUtils.assertTrue(restTemplate != null, "restTemplate must be set.");
    }

    public void setAuthenticationRedirectStrategy(AuthenticationRedirectStrategy authenticationRedirectStrategy) {
        this.authenticationRedirectStrategy = authenticationRedirectStrategy;
    }

    public void setGlobalRestTgtManager(GlobalRestTgtManager globalRestTgtManager) {
        this.globalRestTgtManager = globalRestTgtManager;
    }

    public void setClientRestLoginUrl(String clientRestLoginUrl) {
        this.clientRestLoginUrl = clientRestLoginUrl;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
//        final String tgt1 = globalRestTgtManager.getTgt(request.getSession(false).getId());
        if (isRequestUrlExcluded(request)) {
            logger.debug("Request is ignored.");
            filterChain.doFilter(request, response);
            return;
        }

        final HttpSession session = request.getSession(false);

        final String tgt = globalRestTgtManager.getTgt(session.getId());
        if (StringUtils.isEmpty(tgt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 已登陆，创建服务票据并重定向页面
        final String serviceUrl = SpringWebUtils.getRequestParam("service");

        // 创建服务票据
        final String st = RestAuthUtils.createStStr(restTemplate, tgt , serviceUrl);

        // 已登陆，重定向页面
        final String urlToRedirectTo = RestAuthUtils.getRedirectUrl(serviceUrl, st);

        logger.debug("redirecting to \"{}\"", urlToRedirectTo);
        this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
    }

    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return !url.equals(clientRestLoginUrl);
    }

    @Override
    public void destroy() {

    }

}
