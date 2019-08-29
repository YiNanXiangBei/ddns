package org.yinan.ddns.server.callback;

import cn.yinan.web.callback.IWebSocketCompleteCallback;

/**
 * @author yinan
 * @date 19-8-9
 */
public class WebSocketCallback implements IWebSocketCompleteCallback {
    /**
     * 针对websocket连接初始化成功之后，将文件数据同步到前台，后序针对meter文件以增量实时保存到后端以及传递到前台
     */
    @Override
    public void call() {

    }
}
