package org.example.buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.Thread.State;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleBoundedBufferTest {


    //SUT
    private SimpleBoundedBuffer buffer;

    @BeforeEach
    public void setUp() {
        this.buffer = createBufferWithSize(10);
    }

    private SimpleBoundedBuffer createBufferWithSize(int maxSize) {
        return new MonitorSynchronizationSimpleBoundedBuffer(maxSize);
    }

    @Test
    void shouldInitTheBufferWithAFixedSize() {
        int size = ThreadLocalRandom.current().nextInt(1, 100);

        this.buffer = createBufferWithSize(size);

        assertEquals(size, buffer.getCapacity());
    }

    @Test
    void shouldAProducerBeAbleToProduce() throws InterruptedException {
        buffer.produce(1);
        buffer.produce(2);
        buffer.produce(3);

        assertEquals(3, buffer.getSize());
    }

    @Test
    void shouldBufferBeEmptyWhenCreated() {
        assertEquals(0, buffer.getSize());
    }

    @Test
    void shouldBufferBeFullWhenMaxSizeIsReached() throws InterruptedException {
        this.buffer = createBufferWithSize(2);
        buffer.produce(1);
        buffer.produce(2);

        assertEquals(2, buffer.getSize());
        assertTrue(buffer.isFull());
    }

    @Test
    void shouldAConsumerWaitToProduceIfBufferIsFull() throws InterruptedException {

        this.buffer = createBufferWithSize(2);
        buffer.produce(1);
        buffer.produce(2);

        Thread producerThread = new Thread(() -> {
            try {
                buffer.produce(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        producerThread.start();
        giveTimeToThreadToProceed();
        assertTrue(isWaiting(producerThread), "The producer thread should be waiting");
        producerThread.interrupt();
    }

    @Test
    void shouldConsumersBeAbleToConsume() throws InterruptedException {
        buffer.produce(1);
        buffer.produce(2);
        buffer.consume();

        assertEquals(1, buffer.getSize());
    }

    @Test
    void shouldConsumersWaitIfBufferIsEmpty() throws InterruptedException {
        Thread consumerThread = new Thread(() -> {
            try {
                buffer.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        consumerThread.start();
        giveTimeToThreadToProceed();
        assertTrue(isWaiting(consumerThread), "The consumer thread should be waiting");
        consumerThread.interrupt();
    }

    private static boolean isWaiting(Thread consumerThread) {
        return consumerThread.getState() == State.TIMED_WAITING || consumerThread.getState() == State.WAITING;
    }

    @Test
    void shouldAProducerWaitingProceedWhenBufferHasSpaceAgain() throws InterruptedException {
        this.buffer = createBufferWithSize(2);
        buffer.produce(1);
        buffer.produce(2);

        Thread producerThread = new Thread(() -> {
            try {
                buffer.produce(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        producerThread.start();
        giveTimeToThreadToProceed();
        assertTrue(isWaiting(producerThread), "The producer thread should be waiting");

        buffer.consume();
        giveTimeToThreadToProceed();
        assertSame(State.TERMINATED, producerThread.getState());
        assertEquals(2, buffer.getSize());
    }

    @Test
    void shouldAConsumerWaitingProceedWhenBufferHasElementsAgain() throws InterruptedException {
        Thread consumerThread = new Thread(() -> {
            try {
                buffer.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        consumerThread.start();
        giveTimeToThreadToProceed();
        assertTrue(isWaiting(consumerThread), "The consumer thread should be waiting");

        buffer.produce(1);
        giveTimeToThreadToProceed();
        assertSame(State.TERMINATED, consumerThread.getState());
    }

    @Test
    void concurrentProducersShouldNeverProduceMoreThanMaxSize() throws InterruptedException {
        this.buffer = createBufferWithSize(1);
        for (int i = 0; i < 1_000_000; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    buffer.produce(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture.runAsync(() -> {
                try {
                    buffer.consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture.runAsync(() -> {
                try {
                    buffer.produce(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        giveTimeToThreadToProceed();
        giveTimeToThreadToProceed();

        assertEquals(1, buffer.getSize());
    }

    private static void giveTimeToThreadToProceed() throws InterruptedException {
        Thread.sleep(200);
    }
}
