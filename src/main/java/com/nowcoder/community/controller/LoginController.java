package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @RequestMapping(path="/register", method= RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path="/login", method= RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     *
     * @param model 往model里存数据
     * @param user 传入的有username,password,email.这仨会自动封装成user对象，因为它们名字跟user属性名字一致
     * @return 返回视图的名字
     */
    @RequestMapping(path="/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        //注册成功
        if(map == null || map.isEmpty()) {
            //提示注册成功（第三方页面operate-result.html），并跳到首页
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result"; // 去处理一下模版
        } else {
            // 注册失败 返回提示信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register"; // 表单提交方法、路径、提交按钮; 重新回到register页面默认值的显示，提示错误信息
        }

    }

    // url: http://localhost:8080/community/activation/101/code
    @RequestMapping(path="/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPETITION) {
            model.addAttribute("msg", "无效操作！此用户已被激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }
}
