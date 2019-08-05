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
        syncCounterCache(message, metric.getCounters());
        syncMeterCache(message, metric.getMeters());
        syncGaugeCache(message, metric.getGauges());
        syncHistogramCache(message, metric.getHistograms());
        syncTimerCache(message, metric.getTimers());
    }

    @SuppressWarnings("unchecked")
    private void syncCache(String key, AbstractMe metric, String message) {
        if (cache != null) {
            cache.writingToCache(key, gson.toJson(metric));
        }
        if (callback != null) {
            callback.call(message);
        }
    }

    private void syncCounterCache(String message, Map<String, Counter> metrics) {
        metrics.forEach((key, value) -> {
            syncCache(key, value, message);
        });

    }

    private void syncMeterCache(String message, Map<String, Meter> metrics) {
        metrics.forEach((key, value) -> {
            syncCache(key, value, message);
        });

    }

    private void syncGaugeCache(String message, Map<String, Gauge> metrics) {
        metrics.forEach((key, value) -> {
            syncCache(key, value, message);
        });

    }

    private void syncHistogramCache(String message, Map<String, Histogram> metrics) {
        metrics.forEach((key, value) -> {
            syncCache(key, value, message);
        });

    }

    private void syncTimerCache(String message, Map<String, Timer> metrics) {
        metrics.forEach((key, value) -> {
            syncCache(key, value, message);
        });

    }



}
