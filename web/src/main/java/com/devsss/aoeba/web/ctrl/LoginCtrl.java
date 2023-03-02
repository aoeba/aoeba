package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.UserInfo;
import com.devsss.aoeba.web.dto.BaseResponse;
import com.devsss.aoeba.web.dto.LoginRequest;
import com.devsss.aoeba.login.utils.JwtUtil;
import com.devsss.aoeba.service.service.UserService;
import com.devsss.aoeba.web.dto.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
public class LoginCtrl {

    UserService userService;

    @PostMapping("/login")
    public Mono<BaseResponse<String>> login(@RequestBody LoginRequest request) {
        UserInfo user = userService.getUser(request.getUserName(), request.getPassword());
        final BaseResponse<String> response = new BaseResponse<>();
        if (user == null) {
            response.setCode(RespCode.ERROR.getCode());
            response.setMsg("帐号或密码错误");
        } else {
            response.setCode(RespCode.OK.getCode());
            response.setData(JwtUtil.getToken(user.getUserName(), user.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
        }
        return Mono.just(response);
    }

    @PostMapping("/flashToken")
    public Mono<BaseResponse<String>> flashToken(Authentication authentication) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(RespCode.OK.getCode());
        response.setData(JwtUtil.getToken(authentication.getName(), authentication.getAuthorities()));
        return Mono.just(response);
    }
}
