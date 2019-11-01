package com.learn.client.utils;

import com.learn.client.session.GlobalSessionManager;
import com.learn.client.session.GlobalUserHandler;
import org.jasig.cas.client.session.CustomBaseClientFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CustomClientFilter extends CustomBaseClientFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** custom global session manager 自定义全局会话管理，创建和销毁时调用 */
    private GlobalSessionManager globalSessionManager;
    /** custom global user handler 自定义全局用户信息处理，每次请求都会调用 */
    private GlobalUserHandler globalUserHandler;

    public void setGlobalSessionManager(GlobalSessionManager globalSessionManager) {
        this.globalSessionManager = globalSessionManager;
    }

    public void setGlobalUserHandler(GlobalUserHandler globalUserHandler) {
        this.globalUserHandler = globalUserHandler;
    }

    public CustomClientFilter() {
    }

    public CustomClientFilter(GlobalSessionManager globalSessionManager) {
        this.globalSessionManager = globalSessionManager;
    }

    public CustomClientFilter(GlobalUserHandler globalUserHandler) {
        this.globalUserHandler = globalUserHandler;
    }

    public CustomClientFilter(GlobalSessionManager globalSessionManager, GlobalUserHandler globalUserHandler) {
        this.globalSessionManager = globalSessionManager;
        this.globalUserHandler = globalUserHandler;
    }

    @Override
    protected void customRecordSession(String token, HttpSession session) {
        if (globalSessionManager != null) {
            globalSessionManager.recordSession(token, session);
        }

        logger.debug("custom filter record session execute success.");
    }

    @Override
    protected void customDestroySession(String token) {
        if (globalSessionManager != null) {
            globalSessionManager.destroySession(token);
        }

        logger.debug("custom filter destroy session execute success.");
    }

    @Override
    protected void customBeforeDo(ServletRequest servletRequest, ServletResponse servletResponse) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        Assertion assertion = CustomCommonUtils.getAssertion(request);
        if (globalUserHandler != null) {
            globalUserHandler.globalUserHandler(assertion);
        }

        logger.debug("custom filter before do execute success.");
    }

    @Override
    protected void customAfterDo(ServletRequest servletRequest, ServletResponse servletResponse) {

        logger.debug("custom filter after do execute success.");
    }

}
