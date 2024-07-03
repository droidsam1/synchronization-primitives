package org.example.buffer;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingBoundedBuffer implements SimpleBoundedBuffer {

    private final int maxSize;
    private final ArrayBlockingQueue<Object> elements;

    public ArrayBlockingBoundedBuffer(int size) {
        this.maxSize = size;
        this.elements = new ArrayBlockingQueue<>(size);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getSize() {
        return elements.size();
    }

    public void produce(Object element) throws InterruptedException {
        this.elements.put(element);
    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public Object consume() throws InterruptedException {
        return this.elements.take();
    }
}
