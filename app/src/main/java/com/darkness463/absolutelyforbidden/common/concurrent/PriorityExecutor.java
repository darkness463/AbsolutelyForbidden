/**
 * 
 */
package com.darkness463.absolutelyforbidden.common.concurrent;

import java.util.concurrent.ExecutorService;

public class PriorityExecutor {
    private static final int MAX_POOL_SIZE = 2;
    private static ExecutorService sExecutor;

    public synchronized static ExecutorService getExecutor() {
        if (sExecutor == null) {
            sExecutor = new PriorityThreadPoolExecutor(MAX_POOL_SIZE);
        }

        return sExecutor;
    }
}
