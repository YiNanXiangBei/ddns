package org.yinan.ddns.monitor.callback;

import com.google.gson.Gson;
import org.yinan.ddns.monitor.cache.ICache;
import org.yinan.ddns.monitor.entity.*;
import java.util.Map;

/**
 * @author yinan
 * @date 19-8-1
 */
public class MetricCallback implements ICallback<String> {


    private ICache<String> cache;

    private ICallback callback;

    private Gson gson;

    public MetricCallback(ICache<String> cache, ICallback callback) {
        this.cache = cache;
        this.callback = callback;
        this.gson = new Gson();
    }

    public MetricCallback(ICache<String> cache) {
        this(cache, null);
    }

    public MetricCallback(ICallback callback) {
        this(null, callback);
    }


    /**
     * 向指定位置输出json信息,可以是存储位置，也可以是某个服务位置
     * 将数据放入缓存，同时将数据同步到前端
     * @param message
     */

    @Override
    public void call(String message) {
        Metric metric = gson.fromJson(message, Metric.class);
        syncCounterCache(metric.getCounters());
        syncMeterCache(metric.getMeters());
        syncGaugeCache(metric.getGauges());
        syncHistogramCache(metric.getHistograms());
        syncTimerCache(metric.getTimers());
    }

    @SuppressWarnings("unchecked")
    private void syncCache(String key, AbstractMe metric) {
        String message = gson.toJson(metric);
        if (cache != null) {
            cache.writingToCache(key, message);
        }
        if (callback != null) {
            Message webMsg = new Message(key, message);
            callback.call(webMsg.toString());
        }
    }

    private void syncCounterCache(Map<String, Counter> metrics) {
        metrics.forEach(this::syncCache);
    }

    private void syncMeterCache(Map<String, Meter> metrics) {
        metrics.forEach(this::syncCache);
    }

    private void syncGaugeCache(Map<String, Gauge> metrics) {
        metrics.forEach(this::syncCache);

    }

    private void syncHistogramCache(Map<String, Histogram> metrics) {
        metrics.forEach(this::syncCache);

    }

    private void syncTimerCache(Map<String, Timer> metrics) {
        metrics.forEach(this::syncCache);

    }



}
