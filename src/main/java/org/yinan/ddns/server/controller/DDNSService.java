package org.yinan.ddns.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.yinan.common.config.Config;
import cn.yinan.common.util.CommonUtil;
import cn.yinan.common.util.JsonUtil;
import org.yinan.ddns.server.dns.DNSConfigService;

import java.io.IOException;
import java.util.List;
/**
 * @author yinan
 * @date 19-8-19
 */
public class DDNSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDNSService.class);

    public void saveConfigToFile(List<DNSConfigService.DNSConfigEntity> configs) {

        String config = JsonUtil.object2json(configs);
        try {
            CommonUtil.saveFileToResource(Config.getInstance().getStringValue("dns.server.customer-dns-config-file"), config);
        } catch (IOException e) {
            LOGGER.error("save config to file error: {}", e);
        }
    }

    public void appendConfigToFile(DNSConfigService.DNSConfigEntity newConfig) {
        String config = JsonUtil.object2json(newConfig);
        try {
            CommonUtil.appendFileToResource(Config.getInstance().getStringValue("dns.server.customer-dns-config-file"), config);
        } catch (IOException e) {
            LOGGER.error("save config to file error: {}", e);
        }
    }

}
