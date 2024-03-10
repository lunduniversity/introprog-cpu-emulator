package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProgramCounterTest {

    private ProgramCounter pc;

    @BeforeEach
    void setUp() {
        pc = new ProgramCounter();
    }

    @Test
    void testInitialIndex() {
        assertEquals(0, pc.getCurrentIndex(), "Initially, the index should be 0.");
    }

    @Test
    void testSetAndGetCurrentIndex() {
        int testIndex = 10;
        pc.setCurrentIndex(testIndex);
        assertEquals(testIndex, pc.getCurrentIndex(), "Setting and getting index should work correctly.");
    }

    @Test
    void testNext() {
        int initialIndex = pc.getCurrentIndex();
        int nextIndex = pc.next();
        assertEquals((int) (initialIndex + 1), nextIndex, "Next should increment the index by 1.");
        assertEquals((int) (initialIndex + 1), pc.getCurrentIndex(), "Current index should be incremented.");
    }

    @Test
    void testJumpTo() {
        int newIndex = 20;
        pc.jumpTo(newIndex);
        assertEquals(newIndex, pc.getCurrentIndex(), "Jumping to a new index should work correctly.");
    }

    @Test
    void testHalt() {
        pc.halt();
        assertTrue(pc.isHalted(), "After halt, isHalted should return true.");
    }

    @Test
    void testHaltAndCheckIndex() {
        pc.halt();
        assertEquals(-1, pc.getCurrentIndex(), "After halt, the index should be -1.");
    }
}
