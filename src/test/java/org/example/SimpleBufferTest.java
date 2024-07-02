package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleBufferTest {


    //SUT
    private SimpleBuffer buffer;


    @BeforeEach
    public void setUp() {
        buffer = new SimpleBuffer(0);
    }

    @Test
    void shouldInitTheBufferWithAFixedSize() {
        int size = ThreadLocalRandom.current().nextInt(1, 100);

        buffer = new SimpleBuffer(size);

        assertEquals(size, buffer.getSize());
    }
}
