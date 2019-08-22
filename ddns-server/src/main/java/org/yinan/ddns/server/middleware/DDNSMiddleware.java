package org.yinan.ddns.server.middleware;

import io.netty.handler.codec.http.FullHttpRequest;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.web.middleware.IRequestMiddleware;

/**
 * @author yinan
 * @date 19-8-21
 */
public class DDNSMiddleware implements IRequestMiddleware {
    @Override
    public void preRequest(FullHttpRequest request) throws Exception {
        MetricsManager.newInstance().inc("total_request");
        MetricsManager.newInstance().meter("total_request_average");
    }

    @Override
    public void afterRequest(FullHttpRequest request) throws Exception {
        MetricsManager.newInstance().inc("total_response");
    }
}
