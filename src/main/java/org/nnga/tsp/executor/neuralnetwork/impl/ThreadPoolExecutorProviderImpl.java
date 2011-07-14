package org.nnga.tsp.executor.neuralnetwork.impl;


import org.nnga.tsp.executor.neuralnetwork.ThreadPoolExecutorProvider;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorProviderImpl implements ThreadPoolExecutorProvider {

    private static final int CORESIZE = 10;
    private static final int MAXSIZE = 10;
    private static final int KEEPALIVE = 5;

    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutorProviderImpl() {
        threadPoolExecutor = new ThreadPoolExecutor(CORESIZE, MAXSIZE, KEEPALIVE, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public ThreadPoolExecutor getExecutor() {
        return threadPoolExecutor;
    }
}
