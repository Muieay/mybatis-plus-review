package com.mui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mui.entity.User;
import org.springframework.stereotype.Repository;

//@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
}
