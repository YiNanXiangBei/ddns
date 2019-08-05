package org.yinan.ddns.monitor.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.yinan.ddns.common.config.Config;

import java.util.Arrays;

/**
 * @author yinan
 * @date 19-8-1
 */
public class MetricsManager {

    private MetricRegistry metrics;

    private static MetricsManager manager = new MetricsManager();

    private Meter meter;

    private Counter counter;

    private MetricsManager() {

    }

    public static synchronized MetricsManager newInstance() {
        if (manager == null) {
            manager = new MetricsManager();
        }
        return manager;
    }

    private void initMetrics(MetricRegistry metrics) {
        this.metrics = metrics;
    }

    public static void init(MetricRegistry metrics) {
        String[] countersName = Config.getInstance().getStringValue("monitor.metric.counter",
                "counter").split(",");
        String[] metersName = Config.getInstance().getStringValue("monitor.metric.meter",
                "meter").split(",");
        String[] gaugesName = Config.getInstance().getStringValue("monitor.metric.gauge",
                "meter").split(",");
        String[] hisesName = Config.getInstance().getStringValue("monitor.metric.histogram",
                "historam").split(",");
        String[] timersName = Config.getInstance().getStringValue("monitor.metric.timer",
                "timer").split(",");
        manager.initMetrics(metrics);
        Arrays.stream(countersName).forEach(name -> manager.initCounter(name));
        Arrays.stream(metersName).forEach(name -> manager.initMeter(name));
    }

    public MetricsManager initMeter(String name) {
        meter = metrics.meter(name);
        return this;
    }

    public void meter(String name) {
        Meter tmp = metrics.meter(name);
        tmp.mark();
    }

    public MetricsManager initCounter(String name) {
        counter = metrics.counter(name);
        return this;
    }

    public void inc(String name) {
        Counter tmp = metrics.counter(name);
        tmp.inc();
    }

    public void dec(String name) {
        Counter tmp = metrics.counter(name);
        tmp.dec();
    }

    public void inc(String name, long defVal) {
        Counter tmp = metrics.counter(name);
        tmp.inc(defVal);
    }

    public void dec(String name, long defVal) {
        Counter tmp = metrics.counter(name);
        tmp.dec(defVal);
    }

}