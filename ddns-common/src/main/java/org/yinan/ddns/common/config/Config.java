package org.yinan.ddns.common.config;

/**
 * @author yinan
 * @date 19-6-10
 */
import org.yinan.ddns.common.util.LangUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取配置文件 默认的config.properties 和自定义都支持
 *
 */
public class Config {

    private static final String DEFAULT_CONF = "config.properties";

    private static final Map<String, Config> INSTANCES = new ConcurrentHashMap<String, Config>();

    private Properties configuration = new Properties();

    private Config() {
        initConfig(DEFAULT_CONF);
    }

    private Config(String configFile) {
        initConfig(configFile);
    }

    private void initConfig(String configFile) {
        InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile);
        try {
            configuration.load(is);
            is.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获得Configuration实例。 默认为config.property
     *
     * @return Configuration实例
     */
    public static Config getInstance() {
        return getInstance(DEFAULT_CONF);
    }

    /**
     * 自定义文件解析**.property
     *
     * @param configFile
     * @return
     */
    public static Config getInstance(String configFile) {
        Config config = INSTANCES.get(configFile);
        if (config == null) {
            synchronized (INSTANCES) {
                config = INSTANCES.get(configFile);
                if (config == null) {
                    config = new Config(configFile);
                    INSTANCES.put(configFile, config);
                }
            }
        }
        return config;
    }

    /**
     * 获得配置项。
     *
     * @param key 配置关键字
     * @return 配置项
     */
    public String getStringValue(String key) {
        return configuration.getProperty(key);
    }

    public String getStringValue(String key, String defaultValue) {
        String value = this.getStringValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public int getIntValue(String key, int defaultValue) {
        return LangUtil.parseInt(configuration.getProperty(key), defaultValue);
    }

    public int getIntValue(String key) {
        return LangUtil.parseInt(configuration.getProperty(key));
    }

    public double getDoubleValue(String key, Double defaultValue) {
        return LangUtil.parseDouble(configuration.getProperty(key), defaultValue);
    }

    public double getDoubleValue(String key) {
        return LangUtil.parseDouble(configuration.getProperty(key));
    }

    public long getLongValue(String key, Long defaultValue) {
        return LangUtil.parseLong(configuration.getProperty(key), defaultValue);
    }

    public long getLongValue(String key) {
        return LangUtil.parseLong(configuration.getProperty(key));
    }

    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        return LangUtil.parseBoolean(configuration.getProperty(key), defaultValue);
    }

    public Boolean getBooleanValue(String key) {
        return LangUtil.parseBoolean(configuration.getProperty(key));
    }
}


