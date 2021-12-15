package com.zhanglubin.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhanglubin.gateway.constant.ErrorEnum;
import com.zhanglubin.gateway.pojo.BaseBean;
import com.zhanglubin.gateway.util.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Configuration
public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {
    
    private final ObjectMapper objectMapper;

    private final BaseBean baseBean;
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("系统环境：{}，请求======Ip：{}，url：{}，路径：{}，Gateway全局异常处理：{}"
                ,baseBean.getEv()
                , IpUtil.getIp(exchange.getRequest())
                , exchange.getRequest().getURI()
                , exchange.getRequest().getPath()
                , ex);
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        Map<String, Object> resultMap = new HashMap<>(4);
        // header set
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            HttpStatus status = ((ResponseStatusException) ex).getStatus();
            response.setStatusCode(status);
            resultMap.put("code", status.value());
        }else {
            resultMap.put("code", ErrorEnum.SYSTEM_RED.code());
        }
        resultMap.put("msg", ex.getMessage());
        resultMap.put("data", "");
        return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(resultMap));
                    } catch (JsonProcessingException e) {
                        log.error("Gateway全局异常处理错误", ex);
                        return bufferFactory.wrap(new byte[0]);
                    }
                })
        );
    }
}
