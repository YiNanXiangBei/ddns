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
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.util.CommonUtil;
import org.yinan.ddns.server.dns.Constant;
import org.yinan.ddns.server.dns.DNSCache;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yinan
 * @date 19-8-12
 */
public class DNSHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {
    private DnsNameResolver dnsNameResolver;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery msg) {
        DatagramDnsResponse response = new DatagramDnsResponse(msg.recipient(), msg.sender(), msg.id());
        if (DnsOpCode.QUERY.equals(msg.opCode())) {
            DefaultDnsQuestion dnsQuestion = msg.recordAt(DnsSection.QUESTION);
            final io.netty.util.concurrent.Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> dnsAnswer = dnsNameResolver.query(dnsQuestion);
            dnsAnswer.addListener((GenericFutureListener<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>>) future -> {
                final AddressedEnvelope<DnsResponse, InetSocketAddress> envelope = future.get();
                response.addRecord(DnsSection.QUESTION, new DefaultDnsQuestion(dnsQuestion.name(), dnsQuestion.type()));

                final DnsResponse dnsResponse = envelope.content();
                final int answers = dnsResponse.count(DnsSection.ANSWER);
                for (int i = 0; i < answers; i++) {
                    response.addRecord(DnsSection.ANSWER, i, envelope.content().recordAt(DnsSection.ANSWER, i));
                }

                ctx.writeAndFlush(response).addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        f.channel().close();
                    }
                });
            });
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
                e.printStackTrace();
            }
        });
        return addresses;
    }
}
