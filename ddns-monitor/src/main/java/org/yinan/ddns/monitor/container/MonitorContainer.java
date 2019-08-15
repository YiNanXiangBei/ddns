package org.yinan.ddns.monitor.container;

import com.google.gson.Gson;
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.container.Container;
import org.yinan.ddns.monitor.cache.AbstractCache;
import org.yinan.ddns.monitor.entity.Counter;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.monitor.task.SynchronousTask;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 19-7-31
 */
public class MonitorContainer implements Container {

    private AbstractCache cache;


    private Gson gson;

    public MonitorContainer(AbstractCache cache) {
        this.cache = cache;
        this.gson = new Gson();
    }


    /**
     * 将文件数据放入缓存中，
     * 将缓存中数据同步到metric中
     * 启动中执行器，定时同步缓存数据到本地
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        //加载数据到缓存
        cache.loadCache();
        //初始化metric
        Map<String, String> map = cache.getAllData();
        map.forEach((key, val) -> {
            Counter counter = gson.fromJson(val, Counter.class);
            if (counter != null) {
                MetricsManager.newInstance().initCounter(key).inc(key, counter.getCount());
            }
        });
        //启动定时任务，定期同步数据到文件中
        execTask();
    }

    /**
     * 将缓存数据放入文件中
     */
    @Override
    public void stop() {
        cache.syncData();
    }

    /**
     * 启动定时任务，定期同步数据到文件中
     */
    @SuppressWarnings("unchecked")
    private void execTask() {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(5);
        threadPool.scheduleAtFixedRate(new SynchronousTask(cache), 3,
                Config.getInstance().getLongValue("monitor.metric.rate", 5L), TimeUnit.SECONDS);

    }
}
