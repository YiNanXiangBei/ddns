package org.yinan.ddns.server.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.util.CommonUtil;
import org.yinan.ddns.common.util.CookieUtil;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.web.annotation.Controller;
import org.yinan.ddns.web.annotation.GetMapping;
import org.yinan.ddns.web.response.ResponseInfo;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author yinan
 * @date 19-7-1
 */
@Controller
public class DDNSWebController {

    private final static String FOLDER_PATH = Config.getInstance().getStringValue("monitor.metric.file.path");

    @GetMapping("/index")
    public ResponseInfo index(FullHttpRequest request) {
        MetricsManager.newInstance().inc("yinan_counter");
        MetricsManager.newInstance().meter("test_like");
        String sessionId = CookieUtil.getSessionId(request);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "index");
    }

    @GetMapping("/getMeter")
    public ResponseInfo getMeterData(FullHttpRequest request) throws IOException {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params =  decoder.parameters();
        String meterName = params.get("meterName").get(0);
        String rateName = params.get("rateName").get(0);
        String filePath = FOLDER_PATH + meterName + "_" + CommonUtil.appendDate(rateName) + "." +
                Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
        RandomAccessFile accessFile = new RandomAccessFile(filePath, "r");
        byte[] bytes = new byte[(int)accessFile.length()];
        accessFile.read(bytes);
        accessFile.close();
        String val = new String(bytes, Charset.forName("UTF-8"));
        return ResponseInfo.build(ResponseInfo.CODE_OK, val);
    }



}
