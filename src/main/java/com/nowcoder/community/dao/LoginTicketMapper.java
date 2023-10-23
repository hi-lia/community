package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    /**
     * 实现增删改的功能
     * return： 修改的行数int
     * 不用xml写sql语句，试试用注解
     * @Insert({"","",""})多个字符串拼接
     * @Options() 设置自动生成id
     */
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values (#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test=\"ticket!=null\">",
                "and 1=1",
            "</if>",
            "</script>"

    })
    int updateStatus(String ticket, int status);
}
