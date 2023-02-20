package com.mui.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mui.entity.User;
import com.mui.mapper.UserMapper;
import com.mui.service.UserMapperService;
import org.springframework.stereotype.Service;

@Service
public class UserMapperServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserMapperService {
}
