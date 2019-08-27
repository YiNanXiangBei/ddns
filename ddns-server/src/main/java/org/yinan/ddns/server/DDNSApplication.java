package org.yinan.ddns.server;

import com.codahale.metrics.MetricRegistry;
import org.yinan.ddns.common.container.Container;
import org.yinan.ddns.common.container.ContainerHelper;
import org.yinan.ddns.monitor.cache.AbstractCache;
import org.yinan.ddns.monitor.cache.CacheService;
import org.yinan.ddns.monitor.callback.ICallback;
import org.yinan.ddns.monitor.callback.MetricCallback;
import org.yinan.ddns.monitor.container.MonitorContainer;
import org.yinan.ddns.monitor.metrics.JsonReporter;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.server.callback.FrontCallback;
import org.yinan.ddns.server.callback.WebSocketCallback;
import org.yinan.ddns.server.dns.DNSConfigContainer;
import org.yinan.ddns.web.WebApplicationStarter;
import org.yinan.ddns.web.WebConfigContainer;
import org.yinan.ddns.web.annotation.ComponentScan;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 19-7-1
 */
@ComponentScan("org.yinan.ddns.server.controller")
public class DDNSApplication {
    public static void main(String[] args) {
        WebApplicationStarter.start();
        //初始化顺序比较重要
        ContainerHelper.start(Arrays.asList(metricInit(), new WebConfigContainer(new WebSocketCallback()), new DNSConfigContainer()));
    }

    private static Container metricInit() {
        AbstractCache<String> cache = new CacheService();
        FrontCallback frontCallback = new FrontCallback();
        ICallback<String> callback = new MetricCallback(cache, frontCallback);
        //初始化metric部分功能
        MetricRegistry registry = new MetricRegistry();
        JsonReporter reporter = JsonReporter.forRegistry(registry, callback)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
        MetricsManager.init(registry);
        return new MonitorContainer(cache);
    }

}
