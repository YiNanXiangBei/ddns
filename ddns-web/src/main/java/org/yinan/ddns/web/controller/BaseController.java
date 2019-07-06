package org.yinan.ddns.web.controller;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.util.CookieUtil;
import org.yinan.ddns.common.util.JsonUtil;
import org.yinan.ddns.web.annotation.Controller;
import org.yinan.ddns.web.annotation.PostMapping;
import org.yinan.ddns.web.config.BaseConfig;
import org.yinan.ddns.web.response.ResponseInfo;
import org.yinan.ddns.web.session.SessionManager;

import java.util.Map;
import java.util.Set;

/**
 * @author yinan
 * @date 19-6-13
 */
@Controller
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @PostMapping("/login")
    public ResponseInfo login(FullHttpRequest request) {
        LOGGER.info("get login!");

        String sessionId = CookieUtil.getSessionId(request);

        if (SessionManager.getInstance().validateSession(sessionId)) {
            ResponseInfo responseInfo =  ResponseInfo.build(ResponseInfo.CODE_OK, "login success");
            responseInfo.cookies("JSESSIONID", sessionId);
            return responseInfo;
        }

        byte[] buf = new byte[request.content().readableBytes()];
        request.content().readBytes(buf);
        String config = new String(buf);
        Map<String, String> loginParams = JsonUtil.json2object(config, new TypeToken<Map<String, String>>() {
        });
        if (loginParams == null) {
            return ResponseInfo.build(ResponseInfo.CODE_INVALID_PARAMS, "Error login info");
        }

        String username = loginParams.get("username");
        String password = loginParams.get("password");
        if (username == null || password == null) {
            return ResponseInfo.build(ResponseInfo.CODE_INVALID_PARAMS, "Error username or password");
        }

        if (username.equals(BaseConfig.getInstance().getConfigAdminUsername()) && password.equals(BaseConfig.getInstance().getConfigAdminPassword())) {
            sessionId = SessionManager.getInstance().addSession();
            ResponseInfo responseInfo =  ResponseInfo.build(ResponseInfo.CODE_OK, "login success");
            responseInfo.cookies("JSESSIONID", sessionId);

            return responseInfo;
        }

        return ResponseInfo.build(ResponseInfo.CODE_INVALID_PARAMS, "Error username or password");
    }

    @PostMapping("/logout")
    public ResponseInfo logout(FullHttpRequest request) {
        String sessionId = CookieUtil.getSessionId(request);
        if (SessionManager.getInstance().removeSession(sessionId)) {
            return ResponseInfo.build(ResponseInfo.CODE_OK, "logout success");
        }
        return ResponseInfo.build(ResponseInfo.CODE_SYSTEM_ERROR, "logout failed");
    }



}
