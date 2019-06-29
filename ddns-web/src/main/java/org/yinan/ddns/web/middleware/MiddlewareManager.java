package org.yinan.ddns.web.middleware;

import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.web.exception.ContextException;
import org.yinan.ddns.web.response.ResponseInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 拦截器管理
 * @author yinan
 * @date 19-6-11
 */
public class MiddlewareManager {

    private static Logger logger = LoggerFactory.getLogger(MiddlewareManager.class);

    private static List<IRequestMiddleware> middlewares = new CopyOnWriteArrayList<>();

    /**
     * 添加拦截器
     * @param middleware 拦截器
     */
    public static void addMiddleware(IRequestMiddleware middleware) {
        if (middlewares.contains(middleware)) {
            throw new IllegalArgumentException("Duplicate RequestMiddleware:" + middleware);
        }

        logger.info("add requestMiddleware {}", middleware);
        middlewares.add(middleware);

    }

    /**
     * 执行拦截器处理
     * @param request
     */
    public static ResponseInfo run(FullHttpRequest request) {
        for (IRequestMiddleware middleware : middlewares) {
            try {
                middleware.preRequest(request);
            } catch (Exception ex) {
                if (ex instanceof ContextException) {
                    logger.info("request：[{}] no authority", request.uri());
                    return ResponseInfo.build(((ContextException) ex).getCode(), ex.getMessage());
                }
                logger.error("request error: {}", ex.getMessage());
            }
        }
        return null;
    }
}
