package org.yinan.ddns.monitor.callback;

/**
 * @author yinan
 * @date 19-8-1
 */
public interface ICallback<T> {

    /**
     * 回调方法，将消息回调到指定地方
     * @param message 消息
     */
    void call(T message);
}
