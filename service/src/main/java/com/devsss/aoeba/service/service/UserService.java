package com.devsss.aoeba.service.service;

import com.devsss.aoeba.service.domain.UserInfo;
import com.devsss.aoeba.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class UserService {

    UserMapper userMapper;

    public UserInfo getUser(String username) {
        return userMapper.getUserInfoByName(username);
    }

    public UserInfo getUser(String username, String password) {
        UserInfo userInfo = userMapper.getUserInfo(username, password);
        userInfo.setAuthorities(Collections.singletonList("admin"));
        return userInfo;
    }
}
