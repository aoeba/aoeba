package com.devsss.aoeba.web.handler;

import com.devsss.aoeba.web.dto.BaseResponse;
import com.devsss.aoeba.web.dto.RespCode;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

@Order(-2)
@Component
public class ErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties resources, ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
        super(errorAttributes, resources.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            Throwable error = errorAttributes.getError(request);
            ServerResponse.BodyBuilder builder = ServerResponse.status(HttpStatus.OK);
            // ... additional builder calls
            error.printStackTrace();
            return builder.bodyValue(new BaseResponse<>(RespCode.ERROR, error.getMessage(), null));
        });
    }
}
