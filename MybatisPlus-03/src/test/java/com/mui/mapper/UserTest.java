package com.mui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mui.entity.User;
import com.mui.service.UserMapperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperService userMapperService;

    @Test
    public void test1(){
        List<User> users = userMapper.selectList(null);

        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void test2(){
        User u = userMapperService.getById(1L);

        System.out.println(userMapperService.count());

        for (User user : userMapperService.list()) {
            System.out.println(user);
        }
        System.out.println(u);
    }
}
