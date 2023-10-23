package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {
        // request, name不能为空
        if(request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }

        // 找到cookie中key为name的值
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie:cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
