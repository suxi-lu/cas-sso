package com.learn.client.session;

import com.learn.client.utils.SpringWebUtils;
import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 默认全局会话session管理
 */
public class DefaultGlobalSessionManager implements GlobalSessionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultGlobalSessionManager.LogoutStrategy logoutStrategy = isServlet30() ? new DefaultGlobalSessionManager.Servlet30LogoutStrategy() : new DefaultGlobalSessionManager.Servlet25LogoutStrategy();

    /** Mapping of token IDs and session IDs to HTTP sessions */
    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

    public void setSessionMappingStorage(final SessionMappingStorage storage) {
        this.sessionMappingStorage = storage;
    }

    public SessionMappingStorage getSessionMappingStorage() {
        return this.sessionMappingStorage;
    }

    @Override
    public void recordSession(String token, HttpSession session) {
        try {
            this.sessionMappingStorage.removeBySessionById(session.getId());
        } catch (final Exception e) {
            // ignore if the session is already marked as invalid. Nothing we can do!
        }
        sessionMappingStorage.addSessionById(token, session);

        logger.debug("default record session success");
    }

    @Override
    public void destroySession(HttpSession session) {
        if (session != null) {
            final String sessionID = session.getId();
            this.sessionMappingStorage.removeBySessionById(sessionID);

            logger.debug("Invalidating session [{}]", sessionID);

            try {
                session.invalidate();
            } catch (final IllegalStateException e) {
                logger.debug("Error invalidating session.", e);
            }
            this.logoutStrategy.logout(SpringWebUtils.getRequest());
        }

        logger.debug("default destroy session by sessionId success");
    }

    @Override
    public void destroySession(String token) {
        final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);

        if (session != null) {
            final String sessionID = session.getId();
            logger.debug("Invalidating session [{}] for token [{}]", sessionID, token);

            try {
                session.invalidate();
            } catch (final IllegalStateException e) {
                logger.debug("Error invalidating session.", e);
            }
            this.logoutStrategy.logout(SpringWebUtils.getRequest());
        }

        logger.debug("default destroy session by token success");
    }

    private static boolean isServlet30() {
        try {
            return HttpServletRequest.class.getMethod("logout") != null;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Abstracts the ways we can force logout with the Servlet spec.
     */
    private interface LogoutStrategy {

        void logout(HttpServletRequest request);
    }

    private class Servlet25LogoutStrategy implements DefaultGlobalSessionManager.LogoutStrategy {

        public void logout(final HttpServletRequest request) {
            // nothing additional to do here
        }
    }

    private class Servlet30LogoutStrategy implements DefaultGlobalSessionManager.LogoutStrategy {

        public void logout(final HttpServletRequest request) {
            try {
                request.logout();
            } catch (final ServletException e) {
                logger.debug("Error performing request.logout.");
            }
        }
    }

}
