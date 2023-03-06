package com.devsss.aoeba.service.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class UserInfo {
    @Id
    String userName;
    String password;
    List<String> authorities;
}
