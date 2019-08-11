package org.yinan.ddns.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.yinan.ddns.web.callback.IWebSocketCompleteCallback;

/**
 * @author yinan
 * @date 19-8-3
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private IWebSocketCompleteCallback callback;

    public WebSocketHandler() {

    }

    public WebSocketHandler(IWebSocketCompleteCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WebSocketSupervise.send2All(msg.retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.pipeline().remove(HttpRequestHandler.class);
            if (callback != null) {
                callback.call();
            }
            WebSocketSupervise.addChannel(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
