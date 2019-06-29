package org.yinan.ddns.web.middleware;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 拦截器
 * @author yinan
 * @date 19-6-11
 */
public interface IRequestMiddleware {

    /**
     * 请求预处理
     * @param request 请求
     * @throws Exception
     */
    void preRequest(FullHttpRequest request) throws Exception;

}