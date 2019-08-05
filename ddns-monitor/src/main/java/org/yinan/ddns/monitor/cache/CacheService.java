package org.yinan.ddns.monitor.cache;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.config.Config;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinan
 * @date 19-7-31
 */
public class CacheService extends AbstractCache<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private final static Map<String, String> CACHE = new ConcurrentHashMap<>();

    private final static String FOLDER_PATH = Config.getInstance().getStringValue("monitor.metric.file.path");

    private String[] fileNames;

    public CacheService() {

        init();
    }

    /**
     * 初始化文件目录
     * 判断目录是否存在，如果目录存在
     * 判断文件是否存在
     * 创建相关文件，目录等相关初始化工作
     */
    private void init() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        fileNames = Config.getInstance().getStringValue("monitor.metric.counter").split(",");
        Arrays.stream(fileNames).forEach(fileName -> {
            String filePath = FOLDER_PATH + fileName + "." + Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    LOGGER.error("create file error: {}", e);
                }
            }
        });
    }

    /**
     * 将文件数据放入缓存中
     */
    @Override
    public void loadCache() {
        if (fileNames.length < 1) {
            throw new RuntimeException("can not load file to cache!");
        }
        Arrays.stream(fileNames).forEach(fileName -> {
            String filePath = FOLDER_PATH + fileName + "." + Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
            File file = new File(filePath);
            try {
                RandomAccessFile accessFile = new RandomAccessFile(file, "r");
                byte[] bytes = new byte[(int)accessFile.length()];
                accessFile.read(bytes);
                accessFile.close();
                String val = new String(bytes, Charset.forName("UTF-8"));
                if (!StringUtil.isNullOrEmpty(val)) {
                    writingToCache(fileName, new String(bytes, Charset.forName("UTF-8")));
                }
            } catch (IOException e) {
                LOGGER.error("loadCache read data from file error: {}", e);
            }
        });
    }

    /**
     * 定时同步数据到本地，或者服务停止时同步数据到本地
     */
    @Override
    public void syncData() {
        if (fileNames.length < 1) {
            throw new RuntimeException("can not load file to cache!");
        }
        Arrays.stream(fileNames).forEach(fileName -> {
            String filePath = FOLDER_PATH + fileName + "." + Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
            File file = new File(filePath);
            try {
                RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
                String value = readingFromCache(fileName);
                if (!StringUtil.isNullOrEmpty(value)) {
                    accessFile.write(value.getBytes(Charset.forName("UTF-8")));
                }
                accessFile.close();
            } catch (IOException e) {
                LOGGER.error("syncData write data to file error: {}", e);
            }
        });
    }

    @Override
    public void writingToCache(String key, String value) {
        CACHE.put(key, value);
    }

    @Override
    public String readingFromCache(String key) {
        return CACHE.get(key);
    }

    @Override
    public Map<String, String> getAllData() {
        return new ConcurrentHashMap<>(CACHE);
    }
}
