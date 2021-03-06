package org.yinan.ddns.server.callback;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import cn.yinan.monitor.callback.ICallback;
import cn.yinan.web.WebSocketSupervise;

/**
 * @author yinan
 * @date 19-8-4
 */
public class FrontCallback implements ICallback<String> {

    /**
     * 通过Websocket发送数据到前端
     */
    @Override
    public void call(String message) {
        WebSocketSupervise.send2All(new TextWebSocketFrame(message));
    }
}
