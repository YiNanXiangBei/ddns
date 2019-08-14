package org.yinan.ddns.server.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;
import org.yinan.ddns.common.container.Container;
import org.yinan.ddns.server.dns.handler.DNSHandler;

/**
 * @author yinan
 * @date 19-8-12
 */
public class DNSConfigContainer implements Container {

    private NioEventLoopGroup group;

    public DNSConfigContainer() {
        group = new NioEventLoopGroup();
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
                        }
                    }).option(ChannelOption.SO_BROADCAST, true);
            ChannelFuture future = bootstrap.bind(53).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        group.shutdownGracefully();
    }
}
