package org.example.buffer;

public interface SimpleBoundedBuffer {

    int getCapacity();

    int getSize();

    void produce(Object element) throws InterruptedException;

    boolean isFull();

    Object consume() throws InterruptedException;
}
