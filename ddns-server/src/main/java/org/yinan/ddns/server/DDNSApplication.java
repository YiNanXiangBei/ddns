package org.yinan.ddns.server;

import com.codahale.metrics.MetricRegistry;
import org.yinan.ddns.monitor.cache.AbstractCache;
import org.yinan.ddns.monitor.cache.CacheService;
import org.yinan.ddns.monitor.callback.ICallback;
import org.yinan.ddns.monitor.callback.MetricCallback;
import org.yinan.ddns.monitor.container.MonitorContainer;
import org.yinan.ddns.monitor.metrics.JsonReporter;
import org.yinan.ddns.monitor.metrics.MetricsManager;
import org.yinan.ddns.server.callback.FrontCallback;
import org.yinan.ddns.server.callback.WebSocketCallback;
import org.yinan.ddns.server.routes.DDNSRouteConfig;
import org.yinan.ddns.web.WebConfigContainer;
import org.yinan.ddns.web.routes.RoutesManager;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 19-7-1
 */
public class DDNSApplication {
    public static void main(String[] args) {
        metricInit();

        webInit();
    }

    private static void metricInit() {

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

        new MonitorContainer(cache).start();

    }

    private static void webInit() {
        RoutesManager.INSTANCE.addRouteConfig(new DDNSRouteConfig());
        new WebConfigContainer(new WebSocketCallback()).start();
    }
}
