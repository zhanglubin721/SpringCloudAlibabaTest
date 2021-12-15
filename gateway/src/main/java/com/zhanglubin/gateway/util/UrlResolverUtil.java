package com.zhanglubin.gateway.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;


import java.util.List;

public class UrlResolverUtil {

    private final static PathMatcher MATCHER = new AntPathMatcher();

    /**
     * 验证url是否匹配,支持精确匹配和后缀为*的模糊匹配
     *
     * @param patternPaths
     * @param requestPath
     * @return
     */
    public static boolean check(List<String> patternPaths, String requestPath) {
        if (CollectionUtils.isEmpty(patternPaths)) {
            return false;
        }
        for (String i : patternPaths) {
            if (i.endsWith("*")) {
                i = i.substring(0, i.length() - 1);
                if (MATCHER.matchStart(requestPath, i)) {
                    return true;
                }
            }
            if (MATCHER.match(i, requestPath)) {
                return true;
            }
        }
        return false;
    }

}
