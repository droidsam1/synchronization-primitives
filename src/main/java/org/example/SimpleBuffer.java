package org.example;

import java.util.ArrayDeque;
import java.util.Queue;

public class SimpleBuffer {

    private final int maxSize;
    private final Queue<Object> elements;

    public SimpleBuffer(int size) {
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
        artificialDelayOf(50);
        this.elements.add(element);
    }

    public boolean isFull() {
        return elements.size() == maxSize;
    }

    public void consume() {
        while(elements.isEmpty()) {
            artificialDelayOf(100);
        }
        artificialDelayOf(50);
        this.elements.poll();
    }

    private static void artificialDelayOf(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
