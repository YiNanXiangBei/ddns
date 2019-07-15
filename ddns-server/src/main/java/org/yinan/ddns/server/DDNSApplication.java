package org.yinan.ddns.server;

import org.yinan.ddns.server.routes.DDNSRouteConfig;
import org.yinan.ddns.web.WebConfigContainer;
import org.yinan.ddns.web.routes.RoutesManager;

/**
 * @author yinan
 * @date 19-7-1
 */
public class DDNSApplication {
    public static void main(String[] args) {
        RoutesManager.INSTANCE.addRouteConfig(new DDNSRouteConfig());
        new WebConfigContainer().start();
    }
}
