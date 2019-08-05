package org.yinan.ddns.monitor.task;

import org.yinan.ddns.monitor.cache.ICache;

/**
 * @author yinan
 * @date 19-8-4
 */
public class SynchronousTask implements Runnable {

    private ICache cache;

    public SynchronousTask(ICache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {
        cache.syncData();
    }
}
