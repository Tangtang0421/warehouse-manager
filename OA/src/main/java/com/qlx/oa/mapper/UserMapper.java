package com.qlx.oa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qlx.oa.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> findAll();

}
