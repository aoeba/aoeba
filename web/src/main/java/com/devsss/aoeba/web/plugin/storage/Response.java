package com.devsss.aoeba.web.plugin.storage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private String msg;

    private boolean isSuccessful;

    private Object result;
}
