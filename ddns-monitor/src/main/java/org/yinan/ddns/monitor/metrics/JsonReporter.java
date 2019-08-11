package org.yinan.ddns.monitor.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.monitor.callback.ICallback;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * A metrics reporter for Metrics that dumps metrics periodically into
 *  * a file in JSON format.
 */
public class JsonReporter extends ScheduledReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonReporter.class);
    private final MetricRegistry registry;
    private ObjectWriter jsonWriter;
    private ICallback<String> callback;

    private JsonReporter(MetricRegistry registry, String name, MetricFilter filter,
                         TimeUnit rateUnit, TimeUnit durationUnit, ICallback<String> callback) {
        super(registry, name, filter, rateUnit, durationUnit);
        this.callback = callback;
        this.registry = registry;
    }

    @Override
    public void start(long period, TimeUnit unit) {
        //MetricsModule 第一个参数在meter中有使用到，如果需要使用meter的平均速率，这里单位最小为second
        jsonWriter = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS,
                TimeUnit.MILLISECONDS, false)).writerWithDefaultPrettyPrinter();
        super.start(period, unit);
    }

    @Override
    public void report(SortedMap<String, Gauge> sortedMap, SortedMap<String, Counter> sortedMap1,
                       SortedMap<String, Histogram> sortedMap2, SortedMap<String, Meter> sortedMap3,
                       SortedMap<String, Timer> sortedMap4) {

        String json;
        try {
            json = jsonWriter.writeValueAsString(registry);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to convert json to string ", e);
            return;
        }

        callback.call(json);
    }

    public static Builder forRegistry(MetricRegistry registry, ICallback<String> callback) {
        return new Builder(registry, callback);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private final ICallback<String> callback;
        private TimeUnit rate = TimeUnit.SECONDS;
        private TimeUnit duration = TimeUnit.MILLISECONDS;
        private MetricFilter filter = MetricFilter.ALL;

        private Builder(MetricRegistry registry, ICallback<String> callback) {
            this.registry = registry;
            this.callback = callback;
        }

        public Builder convertRatesTo(TimeUnit rate) {
            this.rate = rate;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit duration) {
            this.duration = duration;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public JsonReporter build() {
            return new JsonReporter(registry, "json-reporter", filter, rate, duration, callback);
        }

    }

}

