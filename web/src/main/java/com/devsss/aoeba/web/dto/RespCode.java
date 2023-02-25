package com.devsss.aoeba.web.dto;

public enum RespCode {

    OK(0),
    ERROR(1);

    private final int code;

    RespCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
