package com.mui;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MybatisKey {
    @Test
    public void testKey(){
        String randomKey = AES.generateRandomKey();
        System.out.println("-----------------------");
        System.out.println(randomKey);
        System.out.println("------------------------");

        String url="jdbc:mysql://localhost:3306/ssm_test?serverTimezone=UTC&userUnicode=true&characterEncoding=UTF-8";
        String username="root";
        String password ="000000";

        String aesUrl = AES.encrypt(url, randomKey);
        String aesUsername = AES.encrypt(username, randomKey);
        String aesPassword = AES.encrypt(password, randomKey);
        System.out.println("url:"+aesUrl);
        System.out.println("username:"+aesUsername);
        System.out.println("password:"+aesPassword);

    }
}
