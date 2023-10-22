package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    // @Param 用于给参数取别名，
    // 如果方法只有一个参数，并且在<if>里使用则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
