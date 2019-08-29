package org.yinan.ddns.server.dns.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.*;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.SequentialDnsServerAddressStreamProvider;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.yinan.monitor.metrics.MetricsManager;
import org.yinan.ddns.server.dns.Constant;
import org.yinan.ddns.server.dns.DNSCache;
import org.yinan.ddns.server.dns.DNSConfigService;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 19-8-12
 */
public class DNSHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {
    private DnsNameResolver dnsNameResolver;

    private static final Logger LOGGER = LoggerFactory.getLogger(DNSHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery msg) {
        DefaultDnsQuestion dnsQuestion = msg.recordAt(DnsSection.QUESTION);
        if (DNSConfigService.containsDomain(dnsQuestion.name())) {
            ctx.fireChannelRead(msg);
        } else {
            DatagramDnsResponse response = new DatagramDnsResponse(msg.recipient(), msg.sender(), msg.id());
            if (DnsOpCode.QUERY.equals(msg.opCode())) {
                MetricsManager.newInstance().inc("query_times");
                final io.netty.util.concurrent.Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> dnsAnswer = dnsNameResolver.query(dnsQuestion);
                dnsAnswer.addListener((GenericFutureListener<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>>) future -> {
                    try {
                        final AddressedEnvelope<DnsResponse, InetSocketAddress> envelope = future.get(10, TimeUnit.SECONDS);
                        response.addRecord(DnsSection.QUESTION, new DefaultDnsQuestion(dnsQuestion.name(), dnsQuestion.type()));
                        final DnsResponse dnsResponse = envelope.content();
                        final int answers = dnsResponse.count(DnsSection.ANSWER);
                        if (answers == 0) {
                            MetricsManager.newInstance().inc("invalid_request");
                        }
                        for (int i = 0; i < answers; i++) {
                            response.addRecord(DnsSection.ANSWER, i, dnsResponse.recordAt(DnsSection.ANSWER, i));
                        }
                    } catch (Exception e) {
                        LOGGER.warn("time out: {}", e.toString());
                    }

                    ctx.writeAndFlush(response).addListener((ChannelFutureListener) f -> {
                        if (!f.isSuccess()) {
                            f.channel().close();
                        }
                    });
                });
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        dnsNameResolver = new DnsNameResolverBuilder()
                .eventLoop(channel.eventLoop())
                .channelType(NioDatagramChannel.class)
                .nameServerProvider(new SequentialDnsServerAddressStreamProvider(getAddress())).build();
    }

    @Override
    public final void channelInactive(
            final ChannelHandlerContext ctx) {
        if (dnsNameResolver != null) {
            dnsNameResolver.close();
        }
    }

    @Override
    public final void exceptionCaught(
            final ChannelHandlerContext ctx,
            final Throwable cause) {
        closeOnFlush(ctx.channel());
    }

    private static void closeOnFlush(
            final Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private List<InetSocketAddress> getAddress(){
        List<String> resources =  (List<String>) DNSCache.get(Constant.DNS_FILE_NAME_KEY);
        List<InetSocketAddress> addresses = new ArrayList<>();
        resources.forEach(resource -> {
            try {
                addresses.add(new InetSocketAddress(InetAddress.getByName(resource), 53));
            } catch (UnknownHostException e) {
                LOGGER.error("analysis address config files error: {}", e);
            }
        });
        return addresses;
    }
}
