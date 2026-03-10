package com.qlx.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qlx.oa.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> findAll();
}
