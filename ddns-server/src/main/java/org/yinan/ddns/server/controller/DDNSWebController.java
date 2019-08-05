package org.yinan.ddns.server.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import org.yinan.ddns.common.util.CookieUtil;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.web.annotation.Controller;
import org.yinan.ddns.web.annotation.GetMapping;
import org.yinan.ddns.web.response.ResponseInfo;

/**
 * @author yinan
 * @date 19-7-1
 */
@Controller
public class DDNSWebController {

    @GetMapping("/index")
    public ResponseInfo index(FullHttpRequest request) {
        MetricsManager.newInstance().inc("counter1");
        MetricsManager.newInstance().meter("meter1");
        String sessionId = CookieUtil.getSessionId(request);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "index");
    }



}
