package com.nowcoder.community.controller;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        // 自己实现用拦截器拦截未登录用户
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONStirng(0, " 已关注！");
    }

    @RequestMapping(path="/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        // 自己实现用拦截器拦截未登录用户
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONStirng(0, " 已取消关注！");
    }

    @RequestMapping(path="/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        // 分页设置
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followeeList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (followeeList != null) {
            for (Map<String, Object> map:followeeList){
                User followee = (User) map.get("user");
                boolean hasFollowed = hasFollowed(followee.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }

        model.addAttribute("users", followeeList);
        return "/site/followee";
    }
    @RequestMapping(path="/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        // 分页设置
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> followerList = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if (followerList != null) {
            for (Map<String, Object> map:followerList){
                User follower = (User) map.get("user");
                boolean hasFollowed = hasFollowed(follower.getId());
                map.put("hasFollowed", hasFollowed);
            }
        }

        model.addAttribute("users", followerList);
        return "/site/follower";
    }
    private boolean hasFollowed(int userId) {
        User LoginUser = hostHolder.getUser();
        if (LoginUser == null) return false;
        return followService.hasFollowed(LoginUser.getId(), ENTITY_TYPE_USER, userId);
    }
}
