package com.zhanglubin.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author zhanglubin
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered {

    /**
     * 执行过滤器中的业务逻辑
     *     对请求参数中的请求头token进行判断
     *      如果存在此参数:代表已经认证成功
     *      如果不存在此参数 : 认证失败.
     *  ServerWebExchange : 相当于请求和响应的上下文(zuul中的RequestContext)
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        修改请求路径示例
//        新建一个ServerHttpRequest装饰器,覆盖需要装饰的方法
//        ServerHttpRequest request = exchange.getRequest().mutate().path("/login/login").build();
//        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request);
//        return chain.filter(exchange.mutate().request(decorator).build());

//        校验是否携带登陆请求头示例
//        System.out.println("执行了自定义的全局过滤器");
//        1.获取请求参数access-token
//        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if (StringUtils.isEmpty(authorization)) {
//            System.out.println("没有登录");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete(); //请求结束
//        }
        //继续执行过滤器链
        return chain.filter(exchange);
    }

    /**
     * 指定过滤器的执行顺序 , 返回值越小,执行优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
