package com.xiwh.paginator.demo.cache;

import com.xiwh.paginator.TestApplication;
import com.xiwh.paginator.cache.CountResultCache;
import com.xiwh.paginator.cache.impl.CountResultCacheImpl;
import org.apache.ibatis.cache.CacheKey;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApplication.class)
class CountCacheTests {

    @Autowired
    CountResultCacheImpl cache;

    @Test
    void cacheTest() throws Exception {
        int count = cache.getCacheCount(new CacheKey(new Object[]{"1"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 1;
            }
        },1000, false);
        System.out.println("count1:"+count);

        count = cache.getCacheCount(new CacheKey(new Object[]{"1"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 11;
            }
        },1000, false);
        System.out.println("count2:"+count);
        Thread.sleep(2000);
        count = cache.getCacheCount(new CacheKey(new Object[]{"1"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 111;
            }
        },2000, false);
        System.out.println("count3:"+count);
        Thread.sleep(2100);
        count = cache.getCacheCount(new CacheKey(new Object[]{"1"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 1111;
            }
        },2000, false);
        System.out.println("count4:"+count);
    }

    @Test
    void cacheTest2() throws Exception {
        int countA = cache.getCacheCount(new CacheKey(new Object[]{"a"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 1;
            }
        },1000, false);

        int countB = cache.getCacheCount(new CacheKey(new Object[]{"b"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 2;
            }
        },5000, false);

        int countC = cache.getCacheCount(new CacheKey(new Object[]{"c"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 3;
            }
        },2999,false);

        int countD = cache.getCacheCount(new CacheKey(new Object[]{"d"}), new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 4;
            }
        },9999,false);

        cache.printCaches();
    }

    private Integer finishedThreads = 0;

    @Test
    void cacheTest3() throws Exception {

        for(int i=0;i<100;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i = 0;i<10;i++) {
                            final int temp= i;
                            int countA = cache.getCacheCount(new CacheKey(new Object[]{"i-"+i}), new Callable<Integer>() {

                                @Override
                                public Integer call() throws Exception {
                                    System.out.println("i-"+temp+" Executed，thread:"+Thread.currentThread().getName());
                                    return temp;
                                }
                            }, (temp+1)*1000, false);
                        }
                        synchronized (finishedThreads){
                            finishedThreads++;
                            if(finishedThreads==10){
                                System.out.println("Finished:");
                                cache.printCaches();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName("thread-"+i);
            thread.start();
        }

        try{
            Thread.sleep(100);
            for(int j=0;j<10;j++){
                Thread.sleep(1000);
                cache.getCacheCount(new CacheKey(new Object[]{"i-"+j}), new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return -1;
                    }
                }, 1000, false);
                System.out.println((j+1)+" second later:");
                cache.printCaches();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void cacheTest4() throws Exception {

        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i = 0;i<10;i++) {
                            final int temp= i*1000;
                            int countA = cache.getCacheCount(new CacheKey(new Object[]{"i-"+i}), new Callable<Integer>() {

                                @Override
                                public Integer call() throws Exception {
                                    return temp;
                                }
                            }, temp+1000, false);
                        }
                        synchronized (finishedThreads){
                            finishedThreads++;
                            if(finishedThreads==10){
                                System.out.println("Finished:");
                                cache.printCaches();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        try{
            Thread.sleep(100);
            for(int j=0;j<10;j++){
                Thread.sleep(1000);
                cache.startClear();
                System.out.println((j+1)+" second later:");
                cache.printCaches();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    AtomicInteger finishedNum = new AtomicInteger();

    @Test
    void cacheTest5() throws Exception {
        Object lock = new Object();
        System.out.println("Begin");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<8;i++){
            long finalStartTime = startTime;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<1000000;i++){
                        final int num = finishedNum.addAndGet(1);
                        try {
                            cache.getCacheCount(new CacheKey(new Object[]{"i-" + num}), new Callable<Integer>() {

                                @Override
                                public Integer call() throws Exception {
//                                    System.out.println("i-"+num+" Executed，thread:"+Thread.currentThread().getName());
                                    return num;
                                }
                            }, (int) (finalStartTime -System.currentTimeMillis()), false);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(num == 8000000){
                            System.out.println(String.format("Finished %s:", num));
                            synchronized (lock){
                                lock.notify();
                            }
                        }
                    }
                }
            });
            thread.setName("thread-"+i);
            thread.start();
        }
        synchronized (lock) {
            lock.wait();
            System.out.println("Insert "+((System.currentTimeMillis()-startTime)/1000)+"s");
            startTime = System.currentTimeMillis();
            System.out.println("Size " + cache.size());
            cache.startClear();
            System.out.println("Clear "+((System.currentTimeMillis()-startTime)/1000)+"s");
            cache.printCaches();

        }
    }
}
