package com.minzheng.blog.utils.cache.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.utils.cache.func.FunctionEntityCache;
import com.minzheng.blog.utils.cache.func.FunctionListCache;
import com.minzheng.blog.utils.cache.func.FunctionSetCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CacheService {

    @Autowired
    private RedisService redisService;

    /**
     *      * 查询单个实体缓存
     * @param key
     * @param timeout
     * @param clz
     * @param function
     * @param <T>
     * @return
     */
    public <T> T getEntityCache(String key, Long timeout, Class<T> clz, FunctionEntityCache<T> function){
        //从redis缓存查询数据，如果查到，直接返回缓存数据
        //如果没有查到，从数据库查询数据，放到缓存，并返回数据
        T obj = null;
        //从缓存获取数据
        String value = (String) redisService.get(key);
        //如果没有数据，从数据库查询
        if (StringUtils.isBlank(value)) {
            obj = function.getCache();
            //查询后放入缓存
            if (obj != null) {
                String search = JSONObject.toJSONString(obj);
                redisService.set(key, search, timeout);
            } else {
                redisService.set(key, null, timeout);
            }
        }else {
            obj = JSON.parseObject(value,clz);
        }
        return obj;
    }

    /**
     * 查询List集合缓存
     * @param key
     * @param timeout
     * @param clz
     * @param function
     * @param <T>
     * @return
     */
    public <T> List<T> getListCache(String key, Long timeout, Class<T> clz, FunctionListCache<T> function) {
        List<T>list = null;
        //从redis缓存查询数据，如果查到，直接返回缓存数据
        //如果没有查到，从数据库查询数据，放到缓存，并返回数据
        String value = (String) redisService.get(key);
        if (StringUtils.isBlank(value)) {
            //从数据库获取数据
            list = function.getCache();
            if (list.isEmpty()) {
                redisService.set(key, null, timeout);
            } else {
                String val = JSONObject.toJSONString(list);
                redisService.set(key, val, timeout);
            }
        } else {
            list = JSON.parseArray(value, clz);
        }
        return list;
    }

    /**
     * 查询List集合缓存
     * @param key
     * @param timeout
     * @param clz
     * @param function
     * @param <T>
     * @return
     */
    public <T> Set<T> getSetCache(String key, Long timeout, Class<T> clz, FunctionSetCache<T> function) {
        Set<T> set = null;
        //从redis缓存查询数据，如果查到，直接返回缓存数据
        //如果没有查到，从数据库查询数据，放到缓存，并返回数据
        String value = (String) redisService.get(key);
        if (StringUtils.isBlank(value)) {
            //从数据库获取数据
            set = function.getCache();
            if (set.isEmpty()) {
                redisService.set(key, null, timeout);
            } else {
                String val = JSONObject.toJSONString(set);
                redisService.set(key, val, timeout);
            }
        } else {
            set = new HashSet(Arrays.asList(JSON.parseArray(value, clz)));
        }
        return set;
    }


    /**
     * 清除缓存
     * @param key
     */
    public void clearCache(String key) {
        if (redisService.hasKey(key)) {
            redisService.del(key);
        }
    }
}
