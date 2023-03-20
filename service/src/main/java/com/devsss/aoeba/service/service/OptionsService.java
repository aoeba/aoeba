package com.devsss.aoeba.service.service;

import com.devsss.aoeba.service.domain.Options;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class OptionsService {

    public static Map<String, String> options = new HashMap<>();

    DatabaseClient databaseClient;

    R2dbcEntityTemplate r2dbcEntityTemplate;

    @PostConstruct
    public void init() {
        // 初始化系统运行设置
        databaseClient.sql("select * from options").fetch().all().mapNotNull(readable -> {
            String key = (String) readable.get("opt_key");
            String value = (String) readable.get("opt_value");
            options.put(key, value);
            log.debug("加载初始化设置信息:{}", key);
            return key;
        }).subscribe();
    }

    public String getUserName() {
        return options.get("userName");
    }

    public String getPassword() {
        return options.get("password");
    }

    public String getQdSetting() {
        return options.get("qdSetting");
    }

    public Mono<Options> saveQdSetting(String setJson) {
        Options option = new Options();
        option.setOptKey("qdSetting");
        option.setOptValue(setJson);
        return r2dbcEntityTemplate.update(option).map(opt -> {
            options.put("qdSetting", setJson);
            return opt;
        });
    }
}
