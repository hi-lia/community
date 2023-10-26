package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {

        return "/site/setting";
    }

    /**
     * 功能：上传文件
     * 请求方法：必须是post
     * 注入：给loginUser设置头像，所以要从hostHolder里获取当前user
     * 参数：MultipartFile对象，SpringMVC专门提供；
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // headerImage为空
        if (headerImage == null) {
            model.addAttribute("error", "您还没有上传图片！");
            return "/site/setting";
        }
        // 文件没错
        // 存的文件名不能用原始的，我们随机生成名字，后缀保持原来的
        String fileName = headerImage.getOriginalFilename();
        //截取后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 图片后缀空的，提示格式不正确
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }
        // 生成随机名字
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确认文件存放位置
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户的头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.pgn
        // 获取当前用户
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream(); // response会负责关闭
                FileInputStream fis = new FileInputStream(fileName); // 我们自己new出来的 手动关闭
        ) {
            //输出 一批批输出
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    /**
     * 功能：修改用户密码
     * 请求方法：post
     * 参数：model, oldPassword, newPassword, userId(hostHolder获取)
     * return: String map用到不？？ 成功：跳转到成功页面；失败：仍在setting页面，并提示错误信息；
     */
    @LoginRequired
    @RequestMapping(path="/updatePassword", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword, String confirmPassword) {

        // 获取当前user
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
        // 修改成功
        if (map == null || map.isEmpty()){
            model.addAttribute("msg", "密码修改成功！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting"; // 修改一下模版
        }
    }

    // 个人主页：当前用户的，别人的
    @RequestMapping(path="/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user", user);
        //点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        return "/site/profile";
    }
}
