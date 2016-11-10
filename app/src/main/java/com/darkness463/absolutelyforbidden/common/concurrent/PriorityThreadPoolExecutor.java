/**
 *
 */
package com.darkness463.absolutelyforbidden.common.concurrent;


import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


class PriorityThreadPoolExecutor extends ThreadPoolExecutor {//外部不可以随便new线程池，因此把class的修饰符设置为package级别
    private static final int INITIAL_QUEUE_CAPACITY = 11;

    public PriorityThreadPoolExecutor(int nThreads) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(INITIAL_QUEUE_CAPACITY,
                        new PriorityTaskComparator()), new DefaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "darkness463-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        if (callable instanceof Priority)
            return new PriorityTask<T>(((Priority) callable).getPriority(),
                    callable);
        else
            return new PriorityTask<T>(Priority.PRIORITY_DEFAULT, callable);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable,
                                               final T value) {
        if (runnable instanceof Priority)
            return new PriorityTask<T>(((Priority) runnable).getPriority(),
                    runnable, value);
        else
            return new PriorityTask<T>(Priority.PRIORITY_DEFAULT, runnable, value);
    }

    private static final class PriorityTask<T> extends FutureTask<T> implements
            Comparable<PriorityTask<T>> {
        private final int mPriority;

        public PriorityTask(final int priority, final Callable<T> tCallable) {
            super(tCallable);

            mPriority = priority;
        }

        public PriorityTask(final int priority, final Runnable runnable,
                            final T result) {
            super(runnable, result);

            mPriority = priority;
        }

        @Override
        public int compareTo(final PriorityTask<T> o) {
            final int priorityDiff = o.mPriority - mPriority;
            return priorityDiff;
        }
    }

    private static class PriorityTaskComparator implements Comparator<Runnable> {
        @Override
        public int compare(final Runnable left, final Runnable right) {
            return ((PriorityTask) left).compareTo((PriorityTask) right);
        }
    }
}
