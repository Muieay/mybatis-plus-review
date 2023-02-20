package com.mui;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mui.entity.User;
import com.mui.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Wrapper;
import java.util.List;

@SpringBootTest
public class WrapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test1(){
        // 查询name不为空的用户，并且邮箱不为空的用户，年龄大于等于12
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper
                .isNotNull("name")
                .isNotNull("email")
                .ge("age",999);

        userMapper.selectList(wrapper).forEach(System.out::println); // 和我们刚才学习的map对比一下
    }

    @Test
    public void test2(){
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.ge("age","20");
        userMapper.selectList(wrapper);
    }
}
