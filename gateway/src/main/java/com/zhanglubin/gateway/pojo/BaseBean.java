package com.zhanglubin.gateway.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RefreshScope
@Data
@ConfigurationProperties(prefix = "filter-dfine")
public class BaseBean {

    /**
     * 黑名单
     */
    private List<String> blackList;

    /**
     * 放行路径
     */
    private List<String> urlWhileList;

    /**
     * 当前环境
     */
    private String ev;

}
