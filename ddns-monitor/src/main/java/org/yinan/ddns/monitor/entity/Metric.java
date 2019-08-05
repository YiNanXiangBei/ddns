package org.yinan.ddns.monitor.entity;

import java.util.Map;

/**
 * @author yinan
 * @date 19-8-4
 */
public class Metric {

    private String version;

    private Map<String, Gauge> gauges;

    private Map<String, Counter> counters;

    private Map<String, Histogram> histograms;

    private Map<String, Meter> meters;

    private Map<String, Timer> timers;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Gauge> getGauges() {
        return gauges;
    }

    public void setGauges(Map<String, Gauge> gauges) {
        this.gauges = gauges;
    }

    public Map<String, Counter> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, Counter> counters) {
        this.counters = counters;
    }

    public Map<String, Histogram> getHistograms() {
        return histograms;
    }

    public void setHistograms(Map<String, Histogram> histograms) {
        this.histograms = histograms;
    }

    public Map<String, Meter> getMeters() {
        return meters;
    }

    public void setMeters(Map<String, Meter> meters) {
        this.meters = meters;
    }

    public Map<String, Timer> getTimers() {
        return timers;
    }

    public void setTimers(Map<String, Timer> timers) {
        this.timers = timers;
    }
}
