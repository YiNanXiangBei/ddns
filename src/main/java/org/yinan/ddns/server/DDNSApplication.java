package org.yinan.ddns.server;

import com.codahale.metrics.MetricRegistry;
import cn.yinan.common.container.Container;
import cn.yinan.common.container.ContainerHelper;
import cn.yinan.monitor.cache.AbstractCache;
import cn.yinan.monitor.cache.CacheService;
import cn.yinan.monitor.callback.ICallback;
import cn.yinan.monitor.callback.MetricCallback;
import cn.yinan.monitor.container.MonitorContainer;
import cn.yinan.monitor.metrics.JsonReporter;
import cn.yinan.monitor.metrics.MetricsManager;
import org.yinan.ddns.server.callback.FrontCallback;
import org.yinan.ddns.server.callback.WebSocketCallback;
import org.yinan.ddns.server.dns.DNSConfigContainer;
import cn.yinan.web.WebApplicationStarter;
import cn.yinan.web.WebConfigContainer;
import cn.yinan.web.annotation.ComponentScan;

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
