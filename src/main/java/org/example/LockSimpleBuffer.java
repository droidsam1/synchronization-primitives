package org.example;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class LockSimpleBuffer implements SimpleBuffer {

    private final int maxSize;
    private final Queue<Object> elements;
    private final ReentrantLock lock = new ReentrantLock();

    public LockSimpleBuffer(int size) {
        this.maxSize = size;
        this.elements = new ArrayDeque<>();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getSize() {
        return elements.size();
    }

    public void produce(Object element) {
        while (elements.size() >= maxSize) {
            artificialDelayOf(1);
        }
        lock.lock();
        try {
            this.elements.add(element);
        } finally {
            lock.unlock();
        }

    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public Object consume() {
        while (elements.isEmpty()) {
            artificialDelayOf(1);
        }
        this.lock.lock();
        try {
            return this.elements.poll();
        } finally {
            this.lock.unlock();
        }
    }

    private static void artificialDelayOf(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
