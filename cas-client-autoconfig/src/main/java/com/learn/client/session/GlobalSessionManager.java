package com.learn.client.session;

import com.learn.client.utils.CustomCommonUtils;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.http.HttpSession;

/**
 * 自定义应用全局会话管理
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/17 0017 13:28
 */
public interface GlobalSessionManager {

    /**
     * 获取当前会话用户信息
     * @param session 当前应用会话
     * @return
     */
    default Assertion getAssertion(final HttpSession session) {
        return CustomCommonUtils.getAssertion(session);
    }

    /**
     * 记录应用全局会话
     * @param token 唯一标识
     * @param session 当前应用会话
     */
    void recordSession(final String token, final HttpSession session);

    /**
     * 销毁应用全局会话
     * @param session 当前应用会话
     */
    void destroySession(final HttpSession session);

    /**
     * 销毁应用全局会话
     * @param token 唯一标识
     */
    void destroySession(final String token);

}
