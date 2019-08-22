package org.yinan.ddns.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author yinan
 * @date 19-8-18
 */
public class IPUtil {

    /**
     * ip地址转换成long型数字
     * 通过将sting的split分割成长度为4的字符串数组
     * 通过左移位操作为每一段数字加权，第一段权为2的24次方，第二段为2的16次方，第三段为2的8次方
     * @param strIp
     * @return
     */
    public static int ipToInt(String strIp) throws UnknownHostException {
        byte[] addr = ipToBytes(strIp);
        //reference  java.net.Inet4Address.Inet4Address
        int address  = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
        return address;
    }

    /**
     * 将ip转为int
     * @param ip
     * @return xxx.xxx.xxx.xxx
     */
    public static String intToIp(int ip) {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((ip >>> 24) & 0xFF);
        addr[1] = (byte) ((ip >>> 16) & 0xFF);
        addr[2] = (byte) ((ip >>> 8) & 0xFF);
        addr[3] = (byte) (ip & 0xFF);
        return bytesToIp(addr);
    }

    /**
     * 将byte数组转为ip字符串
     * @param src
     * @return xxx.xxx.xxx.xxx
     */
    public static String bytesToIp(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }

    /**
     * 将ip字符串转为byte数组,注意:ip不可以是域名,否则会进行域名解析
     * @param ip
     * @return byte[]
     * @throws UnknownHostException
     */
    public static byte[] ipToBytes(String ip) throws UnknownHostException {
        return InetAddress.getByName(ip).getAddress();
    }
}
