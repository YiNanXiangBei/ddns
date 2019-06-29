package org.yinan.ddns.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.container.Container;
import org.yinan.ddns.web.config.BaseConfig;
import org.yinan.ddns.web.routes.BaseRouteConfig;
import org.yinan.ddns.web.routes.IRouteConfig;
import org.yinan.ddns.web.routes.RoutesManager;
import java.util.List;

/**
 * @author yinan
 * @date 19-6-9
 */
public class WebConfigContainer implements Container {

    private static Logger logger = LoggerFactory.getLogger(WebConfigContainer.class);

    private NioEventLoopGroup workGroup;

    private NioEventLoopGroup bossGroup;

    public WebConfigContainer() {

        //配置管理，并发量很小，使用单线程处理网络事件
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(1);
        RoutesManager.INSTANCE.addRouteConfig(new BaseRouteConfig());
    }

    public WebConfigContainer(List<IRouteConfig> routeConfigs) {
        this();
        RoutesManager.INSTANCE.addRouteConfig(routeConfigs);
    }


    @Override
    public void start() {
        logger.info("starting web container ...");
        ServerBootstrap httpServerBootStrap = new ServerBootstrap();
        httpServerBootStrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //解码器，将http请求解码成多个对象，或者将多个对象编码成一个返回
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpRequestHandler());
                    }
                });

        try {
            httpServerBootStrap.bind(BaseConfig.getInstance().getConfigServerBind(),
                    BaseConfig.getInstance().getConfigServerPort());
            logger.info("http server start on port: {}", BaseConfig.getInstance().getConfigServerPort());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //初始化整个web应用，将所有的url全部映射完成
        RoutesManager.INSTANCE.activeRouteConfigs();
    }

    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
