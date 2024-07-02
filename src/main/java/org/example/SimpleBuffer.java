package org.example;

public interface SimpleBuffer {

    int getMaxSize();

    int getSize();

    void produce(Object element) throws InterruptedException;

    boolean isFull();

    Object consume() throws InterruptedException;
}
