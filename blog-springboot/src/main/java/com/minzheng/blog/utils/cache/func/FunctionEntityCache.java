package com.minzheng.blog.utils.cache.func;

/**
 * 单个实体缓存
 * @param <T>
 */
@FunctionalInterface
public interface FunctionEntityCache<T> {
    T getCache();
}
