package com.minzheng.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.minzheng.blog.constant.CommonConst;
import com.minzheng.blog.dao.*;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.entity.UserAbout;
import com.minzheng.blog.entity.UserInfo;
import com.minzheng.blog.service.BlogInfoService;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.service.UniqueViewService;
import com.minzheng.blog.utils.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.minzheng.blog.constant.CommonConst.FALSE;
import static com.minzheng.blog.constant.RedisPrefixConst.*;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class BlogInfoServiceImpl implements BlogInfoService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UniqueViewService uniqueViewService;
    @Autowired
    private RedisService redisService;

    @Autowired
    private UserAboutMapper userAboutMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public BlogHomeInfoDTO getBlogInfo() {
        // 查询博主信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                //.select(UserInfo::getAvatar, UserInfo::getNickname, UserInfo::getIntro)
                .eq(UserInfo::getId, CommonConst.BLOGGER_ID));
        // 查询文章数量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDraft, FALSE)
                .eq(Article::getIsDelete, FALSE));
        // 查询分类数量
        Integer categoryCount = categoryDao.selectCount(null);
        // 查询标签数量
        Integer tagCount = tagDao.selectCount(null);
        // 查询公告
        String value = getNotice();
        //Object value = redisService.get(NOTICE);
       // String notice = Objects.nonNull(value) ? value.toString() : "发布你的第一篇公告吧";
        String notice = ObjectUtil.isNotEmpty(value) ? value : "发布你的第一篇公告吧";
        Object viewsCount = "";
        // 查询访问量
        if (redisService.hasKey(BLOG_VIEWS_COUNT)) {
             viewsCount = redisService.get(BLOG_VIEWS_COUNT);
        } else {
            if (redisService.hasKey(BLOG_VIEWS_COUNT_TEMP)) {
                redisService.set(BLOG_VIEWS_COUNT, redisService.get(BLOG_VIEWS_COUNT_TEMP));
            } else {
                redisService.set(BLOG_VIEWS_COUNT,1);
            }
        }
        // 封装数据
        return BlogHomeInfoDTO.builder()
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .notice(notice)
                .viewsCount(viewsCount.toString())
                .build();
    }

    @Override
    public BlogBackInfoDTO getBlogBackInfo() {
        // 查询访问量
        Integer viewsCount = (Integer) redisService.get(BLOG_VIEWS_COUNT);
        // 查询留言量
        Integer messageCount = messageDao.selectCount(null);
        // 查询用户量
        Integer userCount = userInfoDao.selectCount(null);
        // 查询文章量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getIsDraft, FALSE));
        // 查询一周用户量
        List<UniqueViewDTO> uniqueViewList = uniqueViewService.listUniqueViews();
        // 查询分类数据
        List<CategoryDTO> categoryDTOList = categoryDao.listCategoryDTO();
        // 查询redis访问量前五的文章
        Map<String, Integer> articleViewsMap = redisService.hGetAll(ARTICLE_VIEWS_COUNT);
        // 将文章进行倒序排序
        List<Integer> articleIdList = Objects.requireNonNull(articleViewsMap).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(item -> Integer.valueOf(item.getKey()))
                .collect(Collectors.toList());
        // 提取前五篇文章
        int index = Math.min(articleIdList.size(), 5);
        articleIdList = articleIdList.subList(0, index);
        // 文章为空直接返回
        if (articleIdList.isEmpty()) {
            return BlogBackInfoDTO.builder()
                    .viewsCount(viewsCount)
                    .messageCount(messageCount)
                    .userCount(userCount)
                    .articleCount(articleCount)
                    .categoryDTOList(categoryDTOList)
                    .uniqueViewDTOList(uniqueViewList)
                    .build();
        }
        // 查询文章标题
        List<Article> articleList = articleDao.listArticleRank(articleIdList);
        // 封装浏览量
        List<ArticleRankDTO> articleRankDTOList = articleList.stream().map(article -> ArticleRankDTO.builder()
                .articleTitle(article.getArticleTitle())
                .viewsCount(articleViewsMap.get(article.getId().toString()))
                .build())
                .collect(Collectors.toList());
        return BlogBackInfoDTO.builder()
                .viewsCount(viewsCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryDTOList)
                .uniqueViewDTOList(uniqueViewList)
                .articleRankDTOList(articleRankDTOList)
                .build();
    }

    @Override
    public String getAbout() {
        UserAbout res = cacheService.getEntityCache(ABOUT, CommonConst.ABOUT_TIMEOUT, UserAbout.class, () -> userAboutMapper.selectById(1));
        return ObjectUtil.isNotEmpty(res) ? res.getAboutContent() : "";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAbout(UserAbout userAbout) {
        if (StringUtils.isNotBlank(userAbout.getId().toString())) {
            cacheService.clearCache(ABOUT);
            userAboutMapper.updateById(userAbout);
        } else {
            userAboutMapper.insert(userAbout);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNotice(UserAbout userAbout) {
        if (StringUtils.isNotBlank(userAbout.getId().toString())) {
            cacheService.clearCache(NOTICE);
            userAboutMapper.updateById(userAbout);
        } else {
            userAboutMapper.insert(userAbout);
        }
        //redisService.set(NOTICE, notice);
    }

    @Override
    public String getNotice() {
         UserAbout res = cacheService.getEntityCache(NOTICE, CommonConst.ABOUT_TIMEOUT, UserAbout.class, () -> userAboutMapper.selectById(2));
       // Object value = redisService.get(NOTICE);
        return ObjectUtil.isNotEmpty(res) ? res.getAboutContent() : "发布你的第一篇公告吧";
    }

}
