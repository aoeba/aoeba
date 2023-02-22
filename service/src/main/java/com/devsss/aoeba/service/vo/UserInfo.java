package com.devsss.aoeba.service.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    String username;
    String password;
    List<String> authorities;
}
