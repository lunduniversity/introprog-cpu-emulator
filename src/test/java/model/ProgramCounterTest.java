package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

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
    assertEquals(
        testIndex, pc.getCurrentIndex(), "Setting and getting index should work correctly.");
  }

  @Test
  void testNext() {
    int initialIndex = pc.getCurrentIndex();
    int nextIndex = pc.next();
    assertEquals(
        (int) (initialIndex),
        nextIndex,
        "Next should return the current index before incrementing it.");
    assertEquals(
        (int) (initialIndex + 1), pc.getCurrentIndex(), "Current index should be incremented.");
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

  @Test
  void testReset() {
    pc.setCurrentIndex(10);
    pc.reset();
    assertEquals(0, pc.getCurrentIndex(), "After reset, the index should be 0.");
  }

  @Test
  void testIsHaltedInitially() {
    assertTrue(!pc.isHalted(), "Initially, the program counter should not be halted.");
  }

  @Test
  void testAddListener() {
    ProgramCounterListener listener1 = mock(ProgramCounterListener.class);
    ProgramCounterListener listener2 = mock(ProgramCounterListener.class);
    InOrder inOrder1 = inOrder(listener1);
    InOrder inOrder2 = inOrder(listener2);
    pc.addListener(listener1);
    pc.addListener(listener2);

    pc.setCurrentIndex(10);
    pc.next();
    pc.reset();

    inOrder1.verify(listener1).onProgramCounterChanged(10);
    inOrder1.verify(listener1).onProgramCounterChanged(11);
    inOrder1.verify(listener1).onProgramCounterChanged(0);

    inOrder2.verify(listener2).onProgramCounterChanged(10);
    inOrder2.verify(listener2).onProgramCounterChanged(11);
    inOrder2.verify(listener2).onProgramCounterChanged(0);
  }
}
