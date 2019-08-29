package org.yinan.ddns.server.dns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinan
 * @date 19-8-15
 */
public class DNSCache {

    private static final Map<String, Object> DNS_CACHE = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        DNS_CACHE.put(key, value);
    }


    public static Object get(String key) {
        return DNS_CACHE.get(key);
    }

}
