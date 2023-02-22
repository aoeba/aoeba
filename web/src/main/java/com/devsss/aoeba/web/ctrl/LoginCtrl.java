package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.vo.UserInfo;
import com.devsss.aoeba.web.dto.LoginRequest;
import com.devsss.aoeba.web.dto.LoginResponse;
import com.devsss.aoeba.login.utils.JwtUtil;
import com.devsss.aoeba.service.service.UserService;
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
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        UserInfo user = userService.getUser(request.getUsername(), request.getPassword());
        final LoginResponse response = new LoginResponse();
        response.setSuccessful(true);
        response.setToken(JwtUtil.getToken(user.getUsername(), user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
        return Mono.just(response);
    }

    @PostMapping("/flashToken")
    public Mono<LoginResponse> flashToken(Authentication authentication) {
        LoginResponse response = new LoginResponse();
        response.setSuccessful(true);
        response.setToken(JwtUtil.getToken(authentication.getName(), authentication.getAuthorities()));
        return Mono.just(response);
    }
}
