package com.minzheng.blog.service;

import com.minzheng.blog.dto.BlogBackInfoDTO;
import com.minzheng.blog.dto.BlogHomeInfoDTO;
import com.minzheng.blog.entity.UserAbout;


/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
public interface BlogInfoService  {

    /**
     * 获取首页数据
     * @return 博客首页信息
     */
    BlogHomeInfoDTO getBlogInfo();

    /**
     * 获取后台首页数据
     * @return 博客后台信息
     */
    BlogBackInfoDTO getBlogBackInfo();

    /**
     * 获取关于我内容
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 修改关于我内容
     * @param aboutContent 关于我内容
     */
    void updateAbout(UserAbout aboutContent);

    /**
     * 修改公告
     * @param aboutContent 公告
     */
    void updateNotice(UserAbout aboutContent);

    /**
     * 后台查看公告
     * @return 公告
     */
    String getNotice();

}
