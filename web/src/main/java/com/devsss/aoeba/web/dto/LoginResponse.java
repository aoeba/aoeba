package com.devsss.aoeba.web.dto;

import lombok.Data;

@Data
public class LoginResponse {
    boolean successful;
    String token;
}
