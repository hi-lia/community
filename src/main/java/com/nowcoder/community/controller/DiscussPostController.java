package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 异步
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 1帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // 2作者
        // discussPost里的userId: 查到user
        // 1)关联查询 在mapper里面可以做到。效率高，但是有冗余
        // 2）两次查询：效率低一点，优化：从redis缓存中取
        User user = userService.findById(discussPost.getUserId());
        model.addAttribute("user", user);
        // 4 点赞
        // 4.1 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 4.2 点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST,discussPostId );
        model.addAttribute("likeStatus", likeStatus);

        // 3评论
        // 3.1 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        // 3.2 评论VO列表(用于返回给页面)包括comment本身，comment对应的user，comment对应的replies
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 作者
                commentVO.put("user", userService.findById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus", likeStatus);
                // 回复
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replayVOList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply:replyList) {
                        // 回复VO
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复
                        replyVO.put("reply", reply);
                        // 作者
                        replyVO.put("user", userService.findById(reply.getUserId()));
                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);
                        // target
                        User target = reply.getTargetId() == 0 ? null : userService.findById(reply.getTargetId());
                        replyVO.put("target", target);

                        replayVOList.add(replyVO);
                    }
                }
                commentVO.put("replys", replayVOList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);
                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments", commentVOList);

        return "/site/discuss-detail";
    }

}
