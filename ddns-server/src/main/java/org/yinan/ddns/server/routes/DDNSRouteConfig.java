package org.yinan.ddns.server.routes;

import org.yinan.ddns.web.routes.IRouteConfig;

import java.io.IOException;

/**
 * @author yinan
 * @date 19-7-1
 */
public class DDNSRouteConfig implements IRouteConfig {

    private final static String PACKAGE_NAME = "org.yinan.ddns.server.controller";

    @Override
    public void init() throws IOException, ClassNotFoundException {
        init0(PACKAGE_NAME);
    }
}
