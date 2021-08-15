package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.dao.UserAboutMapper;
import com.minzheng.blog.entity.UserAbout;
import com.minzheng.blog.service.UserAboutService;
import org.springframework.stereotype.Service;

@Service
public class UserAboutServiceImpl extends ServiceImpl<UserAboutMapper, UserAbout> implements UserAboutService{


}
