package com.homepanel.core.executor;

import java.util.concurrent.Callable;

public abstract class PriorityCallable<V> implements Callable<V> {

    private PriorityThreadPoolExecutor.PRIORITY priority;

    public PriorityThreadPoolExecutor.PRIORITY  getPriority() {
        return priority;
    }

    private void setPriority(PriorityThreadPoolExecutor.PRIORITY  priority) {
        this.priority = priority;
    }

    public PriorityCallable(PriorityThreadPoolExecutor.PRIORITY  priority) {
        setPriority(priority);
    }
}