package org.yinan.ddns.server.controller;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yinan.ddns.common.config.Config;
import org.yinan.ddns.common.util.CommonUtil;
import org.yinan.ddns.common.util.JsonUtil;
import org.yinan.ddns.server.dns.Constant;
import org.yinan.ddns.server.dns.DNSCache;
import org.yinan.ddns.server.dns.DNSConfigService;
import org.yinan.ddns.web.annotation.Controller;
import org.yinan.ddns.web.annotation.GetMapping;
import org.yinan.ddns.web.annotation.PostMapping;
import org.yinan.ddns.web.response.ResponseInfo;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yinan
 * @date 19-7-1
 */
@Controller
public class DDNSWebController {

    private final static String FOLDER_PATH = Config.getInstance().getStringValue("monitor.metric.file.path");

    private final static String DNS_CONFIG_FILE = Config.getInstance().getStringValue("dns.server.address-file");

    private final static Logger LOGGER = LoggerFactory.getLogger(DDNSWebController.class);

    private DDNSService ddnsService = new DDNSService();

    @GetMapping("/getMeter")
    public ResponseInfo getMeterData(FullHttpRequest request) throws IOException {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params =  decoder.parameters();
        String meterName = params.get("meterName").get(0);
        String rateName = params.get("rateName").get(0);
        String filePath = FOLDER_PATH + meterName + "_" + CommonUtil.appendDate(rateName) + "." +
                Config.getInstance().getStringValue("monitor.metric.fileType", "txt");
        RandomAccessFile accessFile = new RandomAccessFile(filePath, "r");
        byte[] bytes = new byte[(int)accessFile.length()];
        accessFile.read(bytes);
        accessFile.close();
        String val = new String(bytes, Charset.forName("UTF-8"));
        return ResponseInfo.build(ResponseInfo.CODE_OK, val);
    }

    /**
     * 向upper-server.txt文件中添加
     * @param request
     * @return
     */
    @PostMapping("/update-server-host")
    public ResponseInfo updateServerHost(FullHttpRequest request) {
        Map<String, String> configs = getPostParams(request);
        if (configs == null) {
            return ResponseInfo.build(ResponseInfo.CODE_INVALID_PARAMS, "Error request info");
        }
        String host = configs.get("host");
        List<String> hosts =  new ArrayList<>((List<String>) DNSCache.get(Constant.DNS_FILE_NAME_KEY));
        if (hosts.contains(host)) {
            return ResponseInfo.build(ResponseInfo.CODE_OK, "host is duplicate!");
        }
        try {
            CommonUtil.appendFileToResource(DNS_CONFIG_FILE, host);
        } catch (IOException e) {
            LOGGER.error("write file meet some error: {}", e);
            return ResponseInfo.build(ResponseInfo.CODE_SYSTEM_ERROR, "save file failed!");
        }
        hosts.add(host);
        DNSCache.put(Constant.DNS_FILE_NAME_KEY, hosts);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "success!");
    }

    /**
     * 获取所有上游dns服务器地址
     * @param request
     * @return
     */
    @GetMapping("/server-host")
    public ResponseInfo getServerHost(FullHttpRequest request) {
        List<Map<String, String>> listHostMap = new ArrayList<>();
        List<String> hosts =  new ArrayList<>((List<String>) DNSCache.get(Constant.DNS_FILE_NAME_KEY));
        for (String host : hosts) {
            Map<String, String> hostMap = new HashMap<>();
            hostMap.put("host", host);
            listHostMap.add(hostMap);
        }
        return ResponseInfo.build(ResponseInfo.CODE_OK, "success!", listHostMap);
    }

    /**
     * 依据host删除相应数据
     * @param request
     * @return
     */
    @PostMapping("/del/server-host")
    public ResponseInfo deleteServerHost(FullHttpRequest request) {
        Map<String, String> params = getPostParams(request);
        String host = params.get("host");
        List<String> hosts =  new ArrayList<>((List<String>) DNSCache.get(Constant.DNS_FILE_NAME_KEY));
        List<String> newHosts = hosts
                .stream()
                .filter(tmpHost -> !host.equals(tmpHost))
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder(newHosts.get(0));
        for (int i = 1; i < newHosts.size(); i++) {
            builder.append(System.getProperty("line.separator")).append(newHosts.get(i));
        }
        try {
            CommonUtil.saveFileToResource(DNS_CONFIG_FILE, builder.toString());
        } catch (IOException e) {
            LOGGER.error("write file meet some error: {}", e);
            return ResponseInfo.build(ResponseInfo.CODE_SYSTEM_ERROR, "del file failed!");
        }
        DNSCache.put(Constant.DNS_FILE_NAME_KEY, newHosts);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "success!");
    }

    /**
     * 查询所有的配置
     * @param request
     * @return
     */
    @GetMapping("/ddns-config")
    public ResponseInfo getDDNSConfig(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        configs.sort(Comparator.comparingInt(DNSConfigService.DNSConfigEntity::getId));
        return ResponseInfo.build(ResponseInfo.CODE_OK, "success", configs);
    }

    /**
     * 通过id查询对应数据
     * @param request
     * @return
     */
    @GetMapping("/ddns-config/id")
    public ResponseInfo getDDNSConfigById(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params =  decoder.parameters();
        Integer id = Integer.valueOf(params.get("id").get(0));
        DNSConfigService.DNSConfigEntity configEntity = configs
                .stream()
                .filter(dnsConfigEntity -> dnsConfigEntity.getId().equals(id))
                .collect(Collectors.toList())
                .get(0);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "success", configEntity);
    }

    /**
     * 通过id删除指定数据
     * @param request
     * @return
     */
    @PostMapping("/ddns-config/delete")
    public ResponseInfo deleteDDNSConfig(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        Map<String, List<String>> params = getBatchPostParams(request);
        List<Integer> ids = params.get("ids").stream().map(Integer::valueOf).collect(Collectors.toList());
        List<DNSConfigService.DNSConfigEntity> newConfigs = configs
                .stream()
                .filter(dnsConfigEntity -> !ids.contains(dnsConfigEntity.getId()))
                .collect(Collectors.toList());
        DNSCache.put(Constant.DNS_CUSTOMER_CONFIG_KEY, newConfigs);
        ddnsService.saveConfigToFile(newConfigs);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "delete success!");
    }

    /**
     * 更新指定数据
     * @param request
     * @return
     */
    @PostMapping("/ddns-config/update")
    public ResponseInfo updateDDNSConfig(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        Map<String, List<DNSConfigService.DNSConfigEntity>> params = getEntityListParams(request);
        List<DNSConfigService.DNSConfigEntity> newEditConfigs = params.get("config");
        List<Integer> ids = newEditConfigs.stream().map(DNSConfigService.DNSConfigEntity::getId).collect(Collectors.toList());
        List<DNSConfigService.DNSConfigEntity> newConfigs = configs
                .stream()
                .filter(dnsConfigEntity -> !ids.contains(dnsConfigEntity.getId()))
                .collect(Collectors.toList());
        newConfigs.addAll(newEditConfigs);
        DNSCache.put(Constant.DNS_CUSTOMER_CONFIG_KEY, newConfigs);
        ddnsService.saveConfigToFile(newConfigs);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "update success!");
    }

    @PostMapping("/ddns-config/updateByDomain")
    public ResponseInfo updateDDNSConfigByDomain(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        Map<String, String> postParams = getPostParams(request);
        String domain = postParams.get("domain");
        String host = postParams.get("host");
        configs.forEach(config -> {
            if (domain.equals(config.getDomain())) {
                config.setAddress(host);
            }
        });
        DNSCache.put(Constant.DNS_CUSTOMER_CONFIG_KEY, configs);
        ddnsService.saveConfigToFile(configs);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "update success");
    }

    /**
     * 新增数据
     * @param request
     * @return
     */
    @PostMapping("/ddns-config/save")
    public ResponseInfo saveDDNSConfig(FullHttpRequest request) {
        List<DNSConfigService.DNSConfigEntity> configs = getCustomerConfigs();
        Map<String, DNSConfigService.DNSConfigEntity> params = getEntityParams(request);
        DNSConfigService.DNSConfigEntity config = params.get("config");
        config.setId(0);
        DNSConfigService.DNSConfigEntity maxConfig = configs.stream().max(Comparator.comparingInt(DNSConfigService.DNSConfigEntity::getId)).orElse(config);
        config.setId(maxConfig.getId() + 1);
        configs.add(config);
        ddnsService.saveConfigToFile(configs);
        return ResponseInfo.build(ResponseInfo.CODE_OK, "save success!");
    }

    @SuppressWarnings("unchecked")
    private List<DNSConfigService.DNSConfigEntity> getCustomerConfigs() {
        return (List<DNSConfigService.DNSConfigEntity>) DNSCache
                .get(Constant.DNS_CUSTOMER_CONFIG_KEY);
    }

    private Map<String, List<String>> getBatchPostParams(FullHttpRequest request) {
        byte[] buf = new byte[request.content().readableBytes()];
        request.content().readBytes(buf);
        String config = new String(buf);
        return JsonUtil.json2object(config, new TypeToken<Map<String, List<String>>>() {});
    }

    private Map<String, String> getPostParams(FullHttpRequest request) {
        byte[] buf = new byte[request.content().readableBytes()];
        request.content().readBytes(buf);
        String config = new String(buf);
        return JsonUtil.json2object(config, new TypeToken<Map<String, String>>() {});
    }

    private Map<String, DNSConfigService.DNSConfigEntity> getEntityParams(FullHttpRequest request) {
        byte[] buf = new byte[request.content().readableBytes()];
        request.content().readBytes(buf);
        String config = new String(buf);
        return JsonUtil.json2object(config, new TypeToken<Map<String, DNSConfigService.DNSConfigEntity>>() {});
    }

    private Map<String, List<DNSConfigService.DNSConfigEntity>> getEntityListParams(FullHttpRequest request) {
        byte[] buf = new byte[request.content().readableBytes()];
        request.content().readBytes(buf);
        String config = new String(buf);
        return JsonUtil.json2object(config, new TypeToken<Map<String, List<DNSConfigService.DNSConfigEntity>>>() {});
    }

}
