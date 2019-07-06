package org.yinan.ddns.common.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.internal.StringUtil;

import java.util.Set;

/**
 * @author yinan
 * @date 19-7-1
 */
public class CookieUtil {

    public static String getSessionId(FullHttpRequest request) {
        String cookieStr = request.headers().get(HttpHeaderNames.COOKIE);
        if (!StringUtil.isNullOrEmpty(cookieStr)) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.name())) {
                    return cookie.value();
                }
            }
        }
        return "";
    }

}
