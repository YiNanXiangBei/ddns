package org.yinan.ddns.web.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author yinan
 * @date 19-6-10
 */
public class BaseRouteConfig implements IRouteConfig {

    private final static String PACKAGE_NAME = "org.yinan.ddns.web.controller";

    @Override
    public void init() throws IOException, ClassNotFoundException {
        init0(PACKAGE_NAME);
    }

}
