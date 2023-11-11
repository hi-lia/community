package com.nowcoder.community.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 激活重复
     */
    int ACTIVATION_REPETITION = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100; // 100天

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";
    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;
    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";
    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";
    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
