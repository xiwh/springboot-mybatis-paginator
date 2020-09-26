package com.xiwh.paginator.cache;

import org.apache.ibatis.cache.CacheKey;
import java.util.concurrent.Callable;

public interface CountResultCache {

    public int size();

    public void startClear();

    public int getCachedCount(CacheKey cacheKey);

    public boolean removeCache(CacheKey cacheKey);

    public int getCacheCount(CacheKey key, Callable<Integer> countCallable, int expireTime, boolean forceUpdate) throws Exception;

}
