package org.yinan.ddns.monitor.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinan
 * @date 19-7-31
 */
public abstract class AbstractCache<T> implements ICache<T> {

    @Override
    public void loadCache() {

    }

    @Override
    public void syncData() {

    }

    @Override
    public void writingToCache(String key, T value) {

    }

    @Override
    public T readingFromCache(String key) {
        return null;
    }

    /**
     * 获取缓存中所有数据
     * @return
     */
    public Map<String, T> getAllData() {
        return new HashMap<>();
    }

}
