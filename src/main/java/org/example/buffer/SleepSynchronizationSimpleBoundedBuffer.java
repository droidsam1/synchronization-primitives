package org.example.buffer;

import java.util.ArrayDeque;
import java.util.Queue;

public class SleepSynchronizationSimpleBoundedBuffer implements SimpleBoundedBuffer {

    private final int maxSize;
    private final Queue<Object> elements;

    public SleepSynchronizationSimpleBoundedBuffer(int size) {
        this.maxSize = size;
        this.elements = new ArrayDeque<>();
    }

    public int getCapacity() {
        return maxSize;
    }

    public int getSize() {
        return elements.size();
    }

    public void produce(Object element) {
        while (elements.size() >= maxSize) {
            artificialDelayOf(100);
        }
        this.elements.add(element);
    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public Object consume() {
        while (elements.isEmpty()) {
            artificialDelayOf(100);
        }
        return this.elements.poll();
    }

    private static void artificialDelayOf(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
