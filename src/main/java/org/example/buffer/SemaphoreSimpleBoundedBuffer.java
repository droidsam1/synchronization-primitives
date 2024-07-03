package org.example.buffer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SemaphoreSimpleBoundedBuffer implements SimpleBoundedBuffer {

    private final int maxSize;
    private final Queue<Object> elements;
    private final Semaphore availableItems;
    private final Semaphore availableSpaces;

    public SemaphoreSimpleBoundedBuffer(int size) {
        this.maxSize = size;
        this.elements = new ArrayDeque<>();
        this.availableItems = new Semaphore(0);
        this.availableSpaces = new Semaphore(maxSize);
    }

    @Override public int getCapacity() {
        return maxSize;
    }

    @Override public int getSize() {
        return elements.size();
    }

    @Override public void produce(Object element) throws InterruptedException {
        availableSpaces.acquire();
        synchronized (this) {
            elements.add(element);
        }
        availableItems.release();
    }

    @Override public boolean isFull() {
        return elements.size() == maxSize;
    }

    @Override public Object consume() throws InterruptedException {
        availableItems.acquire();
        Object result;
        synchronized (this) {
            result = elements.poll();
        }
        availableSpaces.release();
        return result;
    }
}
