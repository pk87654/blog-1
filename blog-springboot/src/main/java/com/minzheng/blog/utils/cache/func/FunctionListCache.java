package com.minzheng.blog.utils.cache.func;


import java.util.List;

/**
 * list类型缓存
 * @param <T>
 */
public interface FunctionListCache<T> {
    List<T> getCache();
}
