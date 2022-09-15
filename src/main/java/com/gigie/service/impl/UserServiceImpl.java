package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.domain.User;
import com.gigie.mapper.UserMapper;
import com.gigie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
