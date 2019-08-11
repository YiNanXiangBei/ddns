package org.yinan.ddns.monitor.cache;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.util.BeanUtil;
import org.yinan.ddns.common.util.CommonUtil;
import org.yinan.ddns.common.util.JsonUtil;
import org.yinan.ddns.monitor.entity.Message;
import org.yinan.ddns.monitor.entity.Meter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
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

    private String[] counterFileNames;

    private String[] meterFileNames;

    private String[] gaugeFileNames;

    private String[] histogramFileNames;

    private String[] timerFileNames;

    /**
     * meter中需要保存的类型，目前支持：m1_rate,m5_rate,m15_rate,mean_rate 四种
     */
    private String[] meterRateFiles;

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
        counterFileNames = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.counter"))
                .orElse(",")
                .split(",");
        meterFileNames = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.meter"))
                .orElse(",")
                .split(",");
        meterRateFiles = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.meter.rate.file"))
                .orElse(",")
                .split(",");
        gaugeFileNames = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.gauge"))
                .orElse(",")
                .split(",");
        histogramFileNames = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.histogram"))
                .orElse(",")
                .split(",");
        timerFileNames = Optional.ofNullable(Config.getInstance().getStringValue("monitor.metric.timer"))
                .orElse(",")
                .split(",");
        //数组合并
        fileNames = CommonUtil.concatAll(counterFileNames,
                gaugeFileNames, histogramFileNames, timerFileNames);
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
     * 这里仅仅加载counter的数据到缓存，用来同步历史数据问题
     */
    @Override
    public void loadCache() {
        if (counterFileNames.length < 1) {
            LOGGER.info("there metrics type of counter is not exist!");
            return;
        }
        Arrays.stream(counterFileNames).forEach(fileName -> {
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
     * 定时同步数据到本地，或者服务停止时同步数据到本地，约定所有类型数据都同步到本地
     */
    @Override
    public synchronized void syncData() {
        syncCounter();
        syncMeter();
        syncGauge();
        syncHistogram();
        syncTimer();
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

    /**
     * 同步counter类型数据到文件
     */
    private void syncCounter() {
        if (counterFileNames.length < 1) {
            LOGGER.info("there metrics type of counter is not exist!");
            return;
        }
        Arrays.stream(counterFileNames).forEach(fileName -> {
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

    /**
     * 同步meter类型数据到本地
     */
    private void syncMeter() {
        if (meterFileNames.length < 1) {
            LOGGER.info("there metrics type of meter is not exist!");
            return;
        }
        if (meterRateFiles.length < 1) {
            LOGGER.info("there meter type of rate file is not exist!");
            return;
        }
        Arrays.stream(meterFileNames).forEach(fileName -> {
            String value = readingFromCache(fileName);
            Meter meter = JsonUtil.json2object(value, Meter.class);
            Arrays.stream(meterRateFiles).forEach(fileRate -> {
                String filePath = FOLDER_PATH + fileName + "_" + CommonUtil.appendDate(fileRate) + "." +
                        Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
                String key = String.valueOf(System.currentTimeMillis());
                try {
                    double val = (Double)BeanUtil.getValue(meter, fileRate);
                    Message message = new Message(key, (double)Math.round(val * 100) / 100);
                    incDataSave(filePath, message.toArray());
                } catch (Exception e) {
                    LOGGER.error("sync meter data to file error: {}", e);
                }
            });
        });
    }

    /**
     * 同步gauge类型数据
     */
    private void syncGauge() {

    }

    /**
     * 同步histogram类型数据
     */
    private void syncHistogram() {

    }

    /**
     * 同步timer类型数据
     */
    private void syncTimer() {

    }

    private void incDataSave(String filePath, String val) {
        try {
            RandomAccessFile accessFile = new RandomAccessFile(filePath, "rw");
            //提取出相关的名词和时间出来，然后进行增量保存
            if (!StringUtil.isNullOrEmpty(val)) {
                long fileLength = accessFile.length();
                accessFile.seek(fileLength);
                if (fileLength != 0) {
                    val = "," + System.getProperty("line.separator") + val;
                }
                accessFile.write(val.getBytes(Charset.forName("UTF-8")));
            }
            accessFile.close();
        } catch (IOException e) {
            LOGGER.error("syncData write data to file error: {}", e);
        }
    }
}
