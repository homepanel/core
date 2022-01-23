package com.homepanel.core.executor;

import java.util.concurrent.*;

public class PriorityThreadPoolExecutor extends  ThreadPoolExecutor {

    public enum PRIORITY {

        HIGHEST(0),
        HIGH(1),
        MEDIUM(2),
        LOW(3),
        LOWEST(4);

        private int priority;

        public int getPriority() {
            return priority;
        }

        private void setPriority(int priority) {
            this.priority = priority;
        }

        PRIORITY(int priority) {
            setPriority(priority);
        }
    }

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<Runnable>(10, new PriorityFutureComparator()));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        return new PriorityFuture<T>(newTaskFor, ((PriorityCallable) callable).getPriority().getPriority());
    }
}