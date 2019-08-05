package org.yinan.ddns.monitor.cache;

/**
 * @author yinan
 * @date 19-7-31
 * 缓存基本接口
 */
public interface ICache<T> {

    /**
     * 初始化时，将文件或者某个存储位置中的数据同步到缓存
     */
    void loadCache();

    /**
     * 定时，或者任务退出时将缓存中的数据实例化到某处
     */
    void syncData();

    /**
     * 将内存中某处数据存入缓存
     * @param key key
     * @param value 数据
     */
    void writingToCache(String key, T value);

    /**
     * 将缓存中的数据读取到内存中
     * @param key key
     * @return T
     */
    T readingFromCache(String key);


}
