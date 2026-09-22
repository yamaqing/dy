package com.tl.user.provider.config;

import java.util.concurrent.*;

/**
 * Author： roy
 * Description：统一线程池
 **/
public class ThreadPoolManager {
    public static ThreadPoolExecutor commonAsyncPool = new ThreadPoolExecutor(2,
            8,
            3, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000)
            , new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread newThread = new Thread(r);
                    newThread.setName("commonAsyncPool - " + ThreadLocalRandom.current().nextInt(10000));
                    return newThread;
                }
            });
}
