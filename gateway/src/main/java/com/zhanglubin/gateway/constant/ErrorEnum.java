package com.zhanglubin.gateway.constant;

public enum ErrorEnum {
    SYSTEM_RED("网络异常，稍后再试", 500),
    SYSTEM_SKIP("重定向", 301),
    SYSTEM_GREEN("非法参数", 400),
    SYSTEM_PARAM_NOT_EXIST("缺少参数", 402),
    SYSTEM_BLANK("没有权限！", 401),
    SYSTEM_ILLEGAL_TOKEN("非法token，请重新登录！", 403),
    SYSTEM_RESOURCE_NOT_FIND("资源不存在",410),
    SYSTEM_CONFLICT("数据冲突！", 409),
    SYSTEM_OFTEN("操作太频繁，稍后再试", 423);

    private final Integer code;
    private final String value;

    ErrorEnum(String value, Integer code) {
        this.code = code;
        this.value = value;
    }

    public Integer code() {
        return this.code;
    }

    public String value() {
        return this.value;
    }

    public static ErrorEnum getTypeByEnum(Integer code) {
        if (code==null) {
            return null;
        }
        for (ErrorEnum enums : ErrorEnum.values()) {
            if (enums.code().equals(code)) {
                return enums;
            }
        }
        return null;
    }

}
