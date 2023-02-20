package com.mui;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mui.entity.User;
import com.mui.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MybatisPlus02ApplicationTests {

    // 继承了BaseMapper，所有的方法都来自己父类
    // 我们也可以编写自己的扩展方法！
    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
        User user = userMapper.selectById(5);
        System.out.println(user);
    }

    @Test       //插入
    void testInsert(){

        User user = new User();
        user.setName("唐丫丫");
        user.setAge(19);
        user.setEmail("23123@qq.com");

        userMapper.insert(user);
    }

    @Test       //更新
    public void testUpdate(){
        User user = new User();
        user.setId(1L);
        user.setName("欧阳娜娜");
        //动态拼接sql
        userMapper.updateById(user);
    }
    // 测试乐观锁成功！
    @Test
    public void testLocker(){
        // 1、查询用户信息
        User user = userMapper.selectById(1L);
        // 2、修改用户信息
        user.setName("kuangshen");
        user.setEmail("24736743@qq.com");
        // 3、执行更新操作
        userMapper.updateById(user);
    }

    @Test
    public void testOptimisticLocker2(){
        // 线程 1
        User user = new User();
        user.setName("kuangshen111");
        user.setEmail("24736743@qq.com");
        // 模拟另外一个线程执行了插队操作

        User user2 = new User();
        user2.setName("kuangshen222");
        user2.setEmail("24736743@qq.com");

        userMapper.updateById(user2);
        // 自旋锁来多次尝试提交！
        userMapper.updateById(user); // 如果没有乐观锁就会覆盖插队线程的值！
    }


    @Test  //删除
    public void testDeleteById(){
        userMapper.deleteById(5L);
//        userMapper.deleteBatchIds(Arrays.asList(1584153077811576833L,1584154079893770241L));
    }

    @Test
    public void testDeleteMap(){
        Map<String,Object> map=new HashMap<>();
        map.put("id","6");
        userMapper.deleteByMap(map);
    }

    //分页查询
    @Test
    public void testSelectPage(){
        // 参数一：当前页
        // 参数二：页面大小
        Page<User> page = new Page<>(1,3);
        Page<User> userPage = userMapper.selectPage(page, null);

        page.getRecords().forEach(System.out::println);
        System.out.println(page.getTotal());
    }

}
