package org.yinan.ddns.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yinan
 * @date 19-8-24
 */
public class ContainerHelper {

    private static Logger logger = LoggerFactory.getLogger(ContainerHelper.class);

    private static volatile boolean running = true;

    private static List<Container> cachedContainers;

    /**
     * 启动所有容器，同时设置jvm级任务，当jvm关闭之前可以执行该任务清除容器内的数据
     * @param containers
     */
    public static void start(List<Container> containers) {

        cachedContainers = containers;

        // 启动所有容器
        startContainers();

        //jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置过的通过方法addShutdownHook添加的钩子
        //当系统执行完这些钩子之后，jvm才会关闭，所以这些钩子可以在jvm关闭的时候进行内存销毁，对象销毁等操作

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            synchronized (ContainerHelper.class) {

                // 停止所有容器.
                stopContainers();
                running = false;
                ContainerHelper.class.notify();
            }
        }));

        synchronized (ContainerHelper.class) {
            while (running) {
                try {
                    ContainerHelper.class.wait();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void startContainers() {
        //遍历启动容器
        for (Container container : cachedContainers) {
            logger.info("starting container [{}]", container.getClass().getName());
            container.start();
            logger.info("container [{}] started", container.getClass().getName());
        }
    }

    private static void stopContainers() {
        //遍历关闭容器
        for (Container container : cachedContainers) {
            logger.info("stopping container [{}]", container.getClass().getName());
            try {
                container.stop();
                logger.info("container [{}] stopped", container.getClass().getName());
            } catch (Exception ex) {
                logger.warn("container stopped with error", ex);
            }
        }
    }
}
