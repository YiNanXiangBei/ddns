package org.yinan.ddns.server.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.container.Container;
import org.yinan.ddns.common.util.CommonUtil;
import org.yinan.ddns.server.dns.handler.CustomDNSHandler;
import org.yinan.ddns.server.dns.handler.DNSHandler;
import org.yinan.ddns.server.middleware.DDNSMiddleware;
import org.yinan.ddns.web.middleware.MiddlewareManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author yinan
 * @date 19-8-12
 */
public class DNSConfigContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(DNSConfigContainer.class);

    private NioEventLoopGroup group;

    private static final String ADDRESS_FILE = Config.getInstance().getStringValue("dns.server.address-file");

    public DNSConfigContainer() {
        MiddlewareManager.addMiddleware(new DDNSMiddleware());
        group = new NioEventLoopGroup();
        try {
            init();
        } catch (IOException e) {
            LOGGER.error("init error: {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new DatagramDnsQueryDecoder());
                            ch.pipeline().addLast(new DatagramDnsResponseEncoder());
                            ch.pipeline().addLast(new DNSHandler());
                            ch.pipeline().addLast(new CustomDNSHandler());
                        }
                    }).option(ChannelOption.SO_BROADCAST, true);
            ChannelFuture future = bootstrap.bind(Config.getInstance().getIntValue("dns.server.port", 53)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("init dns server error: {}" , e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void stop() {
        group.shutdownGracefully();
    }

    private void init() throws IOException {
        byte[] bytes = CommonUtil.readFileFromResource(ADDRESS_FILE);
        String resource = new String(bytes, StandardCharsets.UTF_8);
        List<String> hosts = Arrays.asList(resource.split(System.getProperty("line.separator")));
        DNSCache.put(Constant.DNS_FILE_NAME_KEY, hosts);
    }
}
