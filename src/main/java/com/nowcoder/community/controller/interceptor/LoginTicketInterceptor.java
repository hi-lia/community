package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 得到cookie, 得到里面的ticket. 封装一下创建一个类CookieUtil
        String ticket = CookieUtil.getValue(request, "ticket");

        // 通过凭证ticket找到userId, 找到user。回到UserService定义通过ticket找到LoginTicket的方法
        if(ticket!=null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 查询凭证有效的话，查询user
            // 1. 凭证不为空
            // 2. Status为0有效
            // 3. Expired 没过期
            if(loginTicket != null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                // 根据凭证查询用户
                User user = userService.findById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user); // 模版前要用user，go to postHandle()
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        // 传给model
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user); // 清除呢？模版之后, 重写afterCompletion
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
