package org.nnga.tsp.executor.neuralnetwork;

import java.util.concurrent.ThreadPoolExecutor;

public interface ThreadPoolExecutorProvider {
    ThreadPoolExecutor getExecutor();
}
