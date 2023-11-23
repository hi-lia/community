package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {
    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    // 在before里面初始化数据
    @Before
    public void before(){
//        System.out.println("before");
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    // 把测试数据从库里删掉，不是真的删除，只是状态改为2删除状态
    @After
    public void after(){
//        System.out.println("after");
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1(){
        System.out.println("test1");

    }

    @Test
    public void test2(){
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post); // 判断查询结果非空
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
        // 也可以重写post的toString方法，然后比较两个对象是否相等
    }

    @Test
    public void testUpdateScore() {
        int rows = discussPostService.updateScore(data.getId(), 2000.00); // 1
        Assert.assertEquals(1, rows);

        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2); // 小数的比较需要指定精度
    }

}
