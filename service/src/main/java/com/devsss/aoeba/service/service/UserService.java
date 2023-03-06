package com.devsss.aoeba.service.service;

import com.devsss.aoeba.service.domain.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class UserService {

    public UserInfo getUser(String username) {
        return new UserInfo();
    }

    public UserInfo getUser(String username, String password) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(username);
        userInfo.setPassword(password);
        userInfo.setAuthorities(Collections.singletonList("admin"));
        return userInfo;
    }
}
