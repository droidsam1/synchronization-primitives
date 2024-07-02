package org.example;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSynchronizationSimpleBuffer implements SimpleBuffer {

    private final int maxSize;
    private final Queue<Object> elements;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public MonitorSynchronizationSimpleBuffer(int size) {
        this.maxSize = size;
        this.elements = new ArrayDeque<>();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getSize() {
        return elements.size();
    }

    public void produce(Object element) throws InterruptedException {
        lock.lock();
        try {
            while (elements.size() >= maxSize) {
                notFull.await();
            }
            this.elements.add(element);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public Object consume() throws InterruptedException {
        lock.lock();
        try {
            while (elements.isEmpty()) {
                notEmpty.await();
            }
            Object element = this.elements.poll();
            notFull.signalAll();
            return element;
        } finally {
            this.lock.unlock();
        }
    }
}
