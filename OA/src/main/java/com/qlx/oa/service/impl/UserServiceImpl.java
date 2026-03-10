package com.qlx.oa.service.impl;

import com.qlx.oa.entity.User;
import com.qlx.oa.mapper.UserMapper;
import com.qlx.oa.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qlx
 * @since 2026-03-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
