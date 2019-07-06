package org.yinan.ddns.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
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
                        //将请求和应答消息编码或者解码为HTTP消息
                        pipeline.addLast(new HttpServerCodec());
                        /*
                         * httpObject 解码器,
                         * 它的作用是将多个消息转换为单一的FullHttpRequest或FullHttpResponse
                         * 对象,原因是HTTP 解码器在每个HTTP消息中会生成多个消息对象 (
                         * HttpRequest/HttpResponse
                         * ,HttpContent,LastHttpContent)
                         */
                        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
                        //主要作用是支持异步发送大的码流(例如大文件传输),但不占用过多的内存,防止JAVA内存溢出
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
