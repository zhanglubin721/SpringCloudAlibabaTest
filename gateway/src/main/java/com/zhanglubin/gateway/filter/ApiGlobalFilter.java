package com.zhanglubin.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.zhanglubin.gateway.constant.ErrorEnum;
import com.zhanglubin.gateway.pojo.BaseBean;
import com.zhanglubin.gateway.util.IpUtil;
import com.zhanglubin.gateway.util.TokenUtil;
import com.zhanglubin.gateway.util.UrlResolverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApiGlobalFilter implements GlobalFilter, Ordered {


    //自定义的配置
    private final BaseBean baseBean;

    /**
     * 拦截所有的请求
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
        Route gatewayUrl = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        URI uri = gatewayUrl.getUri();
        //全局黑名单ip
        if (!CollectionUtils.isEmpty(baseBean.getBlackList()) && baseBean.getBlackList().contains(IpUtil.getIp(exchange.getRequest()))) {
            log.error("系统环境：{}，禁止访问=====Ip：{}，请求服务名：{}，url：{}，路径：{}"
                    , baseBean.getEv()
                    , IpUtil.getIp(exchange.getRequest())
                    , uri.getAuthority()
                    , exchange.getRequest().getURI()
                    , exchange.getRequest().getPath());
            return authErr(response, ErrorEnum.SYSTEM_BLANK.code(), ErrorEnum.SYSTEM_BLANK.value());
        }
        //白名单放行路径（如获取验证码、校验验证码、去登陆接口）
        if (UrlResolverUtil.check(baseBean.getUrlWhileList(), exchange.getRequest().getURI().getPath())) {
            log.info("系统环境：{}，请求======Ip：{}，请求服务名：{}，url：{}，放行路径：{}"
                    , baseBean.getEv()
                    , IpUtil.getIp(exchange.getRequest())
                    , uri.getAuthority()
                    , exchange.getRequest().getURI()
                    , exchange.getRequest().getPath());
            return chain.filter(exchange);
        }

        //Token认证校验
        String authorizationToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(authorizationToken)) {
            log.error("系统环境：{}，禁止访问=====Ip：{}，请求服务名：{}，url：{}，路径：{}"
                    , baseBean.getEv()
                    , IpUtil.getIp(exchange.getRequest())
                    , uri.getAuthority()
                    , exchange.getRequest().getURI()
                    , exchange.getRequest().getPath());
            return authErr(response, ErrorEnum.SYSTEM_BLANK.code(), ErrorEnum.SYSTEM_BLANK.value());
        }
        boolean isAuthory;
//        方案2
//        JSONObject jsonObject;
        try {
            //检验token
            isAuthory = TokenUtil.verify(authorizationToken);
//            jsonObject = TokenUtil.verify(authorizationToken);
        } catch (Exception e) {
            log.error("系统环境：{}，禁止访问=====Ip：{}，请求服务名：{}，url：{}，路径：{}，异常：{}"
                    , baseBean.getEv()
                    , IpUtil.getIp(exchange.getRequest())
                    , uri.getAuthority()
                    , exchange.getRequest().getURI()
                    , exchange.getRequest().getPath()
                    , e);
            return authErr(response, ErrorEnum.SYSTEM_ILLEGAL_TOKEN.code(), ErrorEnum.SYSTEM_ILLEGAL_TOKEN.value());
        }

//        JSONObject finalJsonObject = jsonObject;
//        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
//            httpHeader.set("SID", finalJsonObject.getString("SID"));
//            httpHeader.set("DB_TAG", finalJsonObject.getString("DB_TAG"));
//            httpHeader.set("UID", finalJsonObject.getString("UID"));
//        };
//        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
//        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
//        return chain.filter(build);
        if (isAuthory) {
            return chain.filter(exchange);
        } else {
            return authErr(response, ErrorEnum.SYSTEM_ILLEGAL_TOKEN.code(), ErrorEnum.SYSTEM_ILLEGAL_TOKEN.value());
        }
    }


    private Mono<Void> authErr(ServerHttpResponse response, int code, String msg) {
        JSONObject message = new JSONObject();
        // 响应状态
        message.put("code", code);
        // 响应内容
        message.put("msg", msg);
        // 转换响应消息内容对象为字节
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        // 设置响应对象状态码 200
        response.setStatusCode(HttpStatus.OK);
        // 设置响应对象内容并且指定编码，否则在浏览器中会中文乱码Content-Type:
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 返回响应对象
        return response.writeWith(Mono.just(buffer));
    }

    /**
     *负责filter的顺序，数字越小越优先，越靠前
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
