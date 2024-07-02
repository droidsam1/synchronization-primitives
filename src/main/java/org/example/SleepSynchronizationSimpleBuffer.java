package org.example;

import java.util.ArrayDeque;
import java.util.Queue;

public class SleepSynchronizationSimpleBuffer implements SimpleBuffer {

    private final int maxSize;
    private final Queue<Object> elements;

    public SleepSynchronizationSimpleBuffer(int size) {
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
            artificialDelayOf(100);
        }
        synchronized (this){
            this.elements.add(element);
        }

    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public Object consume() {
        while (elements.isEmpty()) {
            artificialDelayOf(100);
        }
        artificialDelayOf(50);
        synchronized (this){
            return this.elements.poll();
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
