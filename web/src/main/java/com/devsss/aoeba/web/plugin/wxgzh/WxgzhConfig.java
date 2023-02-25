package com.devsss.aoeba.web.plugin.wxgzh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("wxgzh")
public class WxgzhConfig {

    private String appid;
    private String secret;
}

