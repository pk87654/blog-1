package com.minzheng.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minzheng.blog.entity.UserAbout;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAboutMapper extends BaseMapper<UserAbout> {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAbout record);

    int insertSelective(UserAbout record);

    UserAbout selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserAbout record);

    int updateByPrimaryKey(UserAbout record);
}