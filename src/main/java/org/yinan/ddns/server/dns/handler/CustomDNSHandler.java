package org.yinan.ddns.server.dns.handler;

import com.google.gson.reflect.TypeToken;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.dns.*;
import cn.yinan.common.config.Config;
import cn.yinan.common.util.CommonUtil;
import cn.yinan.common.util.IPUtil;
import cn.yinan.common.util.JsonUtil;
import cn.yinan.monitor.metrics.MetricsManager;
import org.yinan.ddns.server.dns.Constant;
import org.yinan.ddns.server.dns.DNSCache;
import org.yinan.ddns.server.dns.DNSConfigService;
import java.net.UnknownHostException;
import java.util.List;
import java.io.IOException;

/**
 * @author yinan
 * @date 19-8-18
 */
public class CustomDNSHandler extends ChannelInboundHandlerAdapter {


    public CustomDNSHandler() throws IOException {
        init();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnknownHostException {
        if (msg instanceof DatagramDnsQuery) {
            DatagramDnsQuery newMsg = (DatagramDnsQuery) msg;
            if (DnsOpCode.QUERY.equals(newMsg.opCode())) {
                MetricsManager.newInstance().inc("query_times");
                MetricsManager.newInstance().inc("personal_ddns_request");
                DefaultDnsQuestion dnsQuestion = newMsg.recordAt(DnsSection.QUESTION);
                DatagramDnsResponse response = new DatagramDnsResponse(newMsg.recipient(), newMsg.sender(), newMsg.id());
                response.addRecord(DnsSection.QUESTION, new DefaultDnsQuestion(dnsQuestion.name(), dnsQuestion.type()));
                String ip = DNSConfigService.getIP(dnsQuestion.name(), newMsg.sender().getAddress().getHostAddress());
                DefaultDnsRawRecord answer = new DefaultDnsRawRecord(dnsQuestion.name(),
                        dnsQuestion.type(), 10, Unpooled.wrappedBuffer(IPUtil.ipToBytes(ip)));
                response.addRecord(DnsSection.ANSWER, answer);
                ctx.writeAndFlush(response).addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        f.channel().close();
                    }
                });
            }
        }
    }


    /**
     * 读取配置文件 dns-config.json
     */
    private void init() throws IOException {
        byte[] bytes = CommonUtil.readFileFromResource(Config.getInstance().getStringValue("dns.server.customer-dns-config-file"));
        List<DNSConfigService.DNSConfigEntity> configs = JsonUtil.json2object(new String(bytes), new TypeToken<List<DNSConfigService.DNSConfigEntity>>(){});
        DNSCache.put(Constant.DNS_CUSTOMER_CONFIG_KEY, configs);
    }
}
