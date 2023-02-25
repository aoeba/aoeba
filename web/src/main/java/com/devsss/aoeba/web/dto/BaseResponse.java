package com.devsss.aoeba.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseResponse<E> {
    int code;
    String msg;
    E data;

    public BaseResponse(RespCode code, String msg, E data) {
        this.code = code.getCode();
        this.msg = msg;
        this.data = data;
    }

}
