package org.yinan.ddns.web;

import org.junit.Test;

/**
 * @author yinan
 * @date 19-6-24
 */
public class WebConfigContainerTest {

    @Test
    public void start() {
        new WebConfigContainer().start();
    }

    @Test
    public void stop() {
    }
}
