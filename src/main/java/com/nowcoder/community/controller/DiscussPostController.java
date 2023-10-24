package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 异步
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 用户未登录
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONStirng(403,"你还没有登录哦！");
        }

        // 用户已登录
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 将来统一处理错误，现在假设不出错
        return CommunityUtil.getJSONStirng(0, "发布成功！");
    }

    @RequestMapping(path="/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        // 1帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // 2作者
        // discussPost里的userId: 查到user
        // 1)关联查询 在mapper里面可以做到。效率高，但是有冗余
        // 2）两次查询：效率低一点，优化：从redis缓存中取
        User user = userService.findById(discussPost.getUserId());
        model.addAttribute("user", user);
        // 3评论
        // 因为暂时还没实现评论功能，先不展示这部分内容
        return "/site/discuss-detail";
    }

}
