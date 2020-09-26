package com.xiwh.paginator.cache.impl;

import com.xiwh.paginator.cache.CountResultCache;
import org.apache.ibatis.cache.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component("countResultCacheImpl")
public class CountResultCacheImpl implements CountResultCache {

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static Lock r = lock.readLock();
    private static Lock w = lock.writeLock();
    private Map<CacheKey,CacheInfo> cacheMap = new HashMap<>();
    private PriorityQueue<CacheInfo> cacheQueue;

    @PostConstruct
    private void init(){
        cacheQueue = new PriorityQueue<>();
    }

    @Override
    public int size(){
        return cacheMap.size();
    }

    @Override
    public void startClear(){
        w.lock();
        long time = System.currentTimeMillis();
        try {
            //Clear expired cache
            CacheInfo oldCacheInfo = cacheQueue.peek();
            while (oldCacheInfo != null) {
                if (time >= oldCacheInfo.expireTime) {
                    cacheMap.remove(oldCacheInfo.cacheKey);
                    cacheQueue.poll();
                    oldCacheInfo = cacheQueue.peek();
                } else {
                    break;
                }
            }
        }finally {
            w.unlock();
        }
    }

    @Override
    public int getCachedCount(CacheKey cacheKey){
        r.lock();
        long time = System.currentTimeMillis();
        try {
            CacheInfo cacheInfo = cacheMap.get(cacheKey);
            //If there is a cache and within the valid time, return directly
            if(cacheInfo != null && time < cacheInfo.expireTime){
                return cacheInfo.count;
            }else{
                return -1;
            }
        }finally {
            r.unlock();
        }
    }

    @Override
    public boolean removeCache(CacheKey cacheKey) {
        w.lock();
        try{
            cacheMap.remove(cacheKey);
            return cacheQueue.remove(cacheKey);
        }finally {
            w.unlock();
        }
    }

    @Override
    public int getCacheCount(CacheKey key, Callable<Integer> countCallable, int expireTime, boolean forceUpdate) throws Exception {
        if(forceUpdate){
            w.lock();
            try{
                long time = System.currentTimeMillis();
                int count = countCallable.call();
                CacheInfo cacheInfo = new CacheInfo(count, key, time+expireTime);
                CacheInfo oldCache = cacheMap.remove(key);
                if(oldCache!=null){
                    cacheQueue.remove(oldCache);
                }
                cacheMap.put(key, cacheInfo);
                cacheQueue.offer(cacheInfo);
                return count;
            }finally {
                w.unlock();
            }
        }
        r.lock();
        long time = System.currentTimeMillis();
        try {
            CacheInfo cacheInfo = cacheMap.get(key);
            //If there is a cache and within the valid time, return directly
            if(cacheInfo != null && time < cacheInfo.expireTime){
                return cacheInfo.count;
            }
        }finally {
            r.unlock();
        }
        w.lock();
        time = System.currentTimeMillis();
        try{
            //Double check
            CacheInfo cacheInfo = cacheMap.get(key);
            if(cacheInfo != null && time >= cacheInfo.expireTime){
                startClear();
                cacheInfo = null;
            }
            if(cacheInfo != null){
                return cacheInfo.count;
            }

            int count = countCallable.call();
            cacheInfo = new CacheInfo(count, key, time+expireTime);
            cacheMap.put(key, cacheInfo);
            cacheQueue.offer(cacheInfo);

            return count;
        }finally {
            w.unlock();
        }
    }

    public void printCaches(){
        synchronized (this){
            StringBuilder builder = new StringBuilder("{CountCache["+cacheQueue.size()+"]}[");
            PriorityQueue<CacheInfo> queue = new PriorityQueue(this.cacheQueue);
            for(int i=0;!queue.isEmpty();i++){
                if(i!=0){
                    builder.append(",");
                }
                CacheInfo cacheInfo = queue.poll();
                builder.append("\r\n\t");
                builder.append(cacheInfo);
            }
            builder.append("\r\n]");
            System.out.println(builder);
        }
    }

    private static class CacheInfo implements Comparable{
        private int count;
        private CacheKey cacheKey;
        private long expireTime;

        public CacheInfo(int count, CacheKey cacheKey, long expireTime){
            this.count = count;
            this.cacheKey = cacheKey;
            this.expireTime = expireTime;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("CacheInfo{");
            sb.append("count=").append(count);
            sb.append(", cacheKey=").append(cacheKey);
            sb.append(", expireTime=").append(expireTime);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public int compareTo(Object o) {
            CacheInfo cacheInfo = (CacheInfo)o;
            return (int) (this.expireTime - cacheInfo.expireTime);
        }
    }
}
