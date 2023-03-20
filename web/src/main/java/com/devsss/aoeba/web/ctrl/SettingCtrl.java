package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.Options;
import com.devsss.aoeba.service.service.OptionsService;
import com.devsss.aoeba.web.dto.BaseResponse;
import com.devsss.aoeba.web.dto.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/setting")
public class SettingCtrl {

    OptionsService optionsService;

    @GetMapping("qdSetting")
    public Mono<BaseResponse<String>> getQdSetting() {
        return Mono.just(new BaseResponse<>(RespCode.OK, "", optionsService.getQdSetting()));
    }

    @PutMapping("qdSetting")
    public Mono<BaseResponse<Options>> updateQdSetting(@RequestBody Map<String, String> json) {
        return optionsService.saveQdSetting(json.get("qdSetting")).map(opt -> new BaseResponse<>(RespCode.OK, "", opt));
    }
}
