package com.homepanel.core.executor;

import java.util.Comparator;

public class PriorityFutureComparator implements Comparator<Runnable> {

    public int compare(Runnable runnableA, Runnable runnableB) {

        if (runnableA == null && runnableB == null)
            return 0;
        else if (runnableA == null)
            return -1;
        else if (runnableB == null)
            return 1;
        else {
            int priorityA = ((PriorityFuture<?>) runnableA).getPriority();
            int priorityB = ((PriorityFuture<?>) runnableB).getPriority();

            return priorityA > priorityB ? 1 : (priorityA == priorityB ? 0 : -1);
        }
    }
}