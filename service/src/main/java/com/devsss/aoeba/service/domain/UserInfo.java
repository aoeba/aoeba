package com.devsss.aoeba.service.domain;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    String username;
    String password;
    List<String> authorities;
}
