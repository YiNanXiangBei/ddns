package org.yinan.ddns.server.dns;
import io.netty.util.internal.StringUtil;
import org.yinan.ddns.common.util.IPUtil;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author yinan
 * @date 19-8-18
 */
public class DNSConfigService {

    private static Integer pos = 0;

    /**
     * 判断是否包含指定域名
     * @param domain
     * @return
     */
    public static boolean containsDomain(String domain) {
        if (StringUtil.isNullOrEmpty(domain)) {
            return false;
        }
        domain = domain.substring(0, domain.length() - 1);
        List<DNSConfigEntity> configs = (List<DNSConfigEntity>)DNSCache.get(Constant.DNS_CUSTOMER_CONFIG_KEY);
        for (DNSConfigEntity config : configs) {
            if (domain.equals(config.getDomain()) && Integer.valueOf(1).equals(config.getEnable())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static String getIP(String domain, String clientIp) throws UnknownHostException {
        String newDomain = domain.substring(0, domain.length() - 1);
        List<DNSConfigEntity> configs = new ArrayList<>((List<DNSConfigEntity>)DNSCache.get(Constant.DNS_CUSTOMER_CONFIG_KEY));
        DNSConfigEntity config = configs.stream().filter(conf -> newDomain.equals(conf.getDomain())).collect(Collectors.toList()).get(0);
        switch (config.getModel()) {
            case 1:
                return pollIP(config.getAddress());
            case 2:
                return hashIP(config.getAddress(), clientIp);
            case 3:
                return randomIP(config.getAddress());
            case 4:
                return config.getAddress();
        }
        return "0.0.0.0";
    }

    /**
     * 随机获取ip数据段中的ip
     * @param ipSegments
     * @return
     */
    private static String randomIP(String ipSegments) throws UnknownHostException {
        String[] ips = ipSegments.split("-");
        //开始ip
        int startIp = IPUtil.ipToInt(ips[0].trim());
        //结束ip
        int endIp = IPUtil.ipToInt(ips[1].trim());
        int interval = endIp - startIp + 1;
        int random = new Random().nextInt(interval);
        return IPUtil.intToIp(startIp + random);
    }

    /**
     * hash方式获取ip数据段中的ip
     * @param ipSegments
     * @return
     */
    private static String hashIP(String ipSegments, String clientIp) throws UnknownHostException {
        int clientHashCode = clientIp.hashCode();
        String[] ips = ipSegments.split("-");
        //开始ip
        int startIp = IPUtil.ipToInt(ips[0].trim());
        //结束ip
        int endIp = IPUtil.ipToInt(ips[1].trim());
        int interval = endIp - startIp + 1;
        int post = clientHashCode % interval;
        return IPUtil.intToIp(startIp + post - 1);
    }

    /**
     * 轮询方式获取ip段中的ip
     * @param ipSegments
     * @return
     */
    private static String pollIP(String ipSegments) throws UnknownHostException {
        String[] ips = ipSegments.split("-");
        //开始ip
        int startIp = IPUtil.ipToInt(ips[0].trim());
        //结束ip
        int endIp = IPUtil.ipToInt(ips[1].trim());
        int interval = endIp - startIp + 1;
        int position;
        synchronized (pos) {
            if (pos >= interval ) {
                pos = 0;
            }
            pos ++;
            position = pos;
        }
        return IPUtil.intToIp(startIp + position - 1);
    }


    public class DNSConfigEntity {
       /**
        * 编号
        */
       private Integer id;

       /**
        * ip地址，或者ip地址段
        */
       private String address;

       /**
        * 域名
        */
       private String domain;

       /**
        * 1-轮询，依次应答
        * 2-IP Hash，与请求来源IP绑定
        * 3-随机
        * 4-默认
        */
       private Integer model;

       /**
        * 是否启用，1:启用,0-不启用
        */
       private Integer enable;

       public Integer getId() {
           return id;
       }

       public void setId(Integer id) {
           this.id = id;
       }

       public String getAddress() {
           return address;
       }

       public void setAddress(String address) {
           this.address = address;
       }

       public String getDomain() {
           return domain;
       }

       public void setDomain(String domain) {
           this.domain = domain;
       }

       public Integer getModel() {
           return model;
       }

       public void setModel(Integer model) {
           this.model = model;
       }

       public Integer getEnable() {
           return enable;
       }

       public void setEnable(Integer enable) {
           this.enable = enable;
       }
   }

}
