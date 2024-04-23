package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public class ProgramCounterTest {

  private ProgramCounter pc;
  private Registry registry;

  @BeforeEach
  void setUp() {
    registry = mock(Registry.class);
    pc = new ProgramCounter(registry);
  }

  @Test
  void testInitialIndex() {
    assertEquals(0, pc.getCurrentIndex(), "Initially, the index should be 0.");
  }

  @Test
  void testSetAndGetCurrentIndex() {
    int testIndex = 10;
    pc.setCurrentIndex(testIndex);
    verify(registry).setRegister(Registry.REG_PC, testIndex);
  }

  @Test
  void testNextReadPC() {
    pc.getCurrentIndex();
    verify(registry).getRegister(Registry.REG_PC);
  }

  @Test
  void testNextIncrementsPC() {
    when(registry.getRegister(Registry.REG_PC)).thenReturn(0);
    int nextIndex = pc.next();
    assertEquals(0, nextIndex, "Next should return the current index before incrementing it.");
    verify(registry).setRegister(Registry.REG_PC, 1);
  }

  @Test
  void testJumpTo() {
    int newIndex = 20;
    pc.jumpTo(newIndex);
    verify(registry).setRegister(Registry.REG_PC, newIndex);
  }

  @Test
  void testHaltSetPCToNegativeOne() {
    when(registry.getRegister(Registry.REG_PC)).thenReturn(-1);
    pc.halt();
    verify(registry).setRegister(Registry.REG_PC, -1);
  }

  @Test
  void testIsHaltedReadsPC() {
    pc.halt();
    verify(registry).getRegister(Registry.REG_PC);
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
    when(registry.getRegister(Registry.REG_PC)).thenReturn(0, 10, 11);

    ProgramCounterListener listener1 = mock(ProgramCounterListener.class);
    ProgramCounterListener listener2 = mock(ProgramCounterListener.class);
    InOrder inOrder1 = inOrder(listener1);
    InOrder inOrder2 = inOrder(listener2);

    pc.addListener(listener1);
    pc.addListener(listener2);

    pc.setCurrentIndex(10);
    pc.next();
    pc.reset();

    verify(registry).setRegister(Registry.REG_PC, 10);
    verify(registry).setRegister(Registry.REG_PC, 11);
    verify(registry).setRegister(Registry.REG_PC, 0);

    inOrder1.verify(listener1).onProgramCounterChanged(0, 10);
    inOrder1.verify(listener1).onProgramCounterChanged(10, 11);
    inOrder1.verify(listener1).onProgramCounterChanged(11, 0);

    inOrder2.verify(listener2).onProgramCounterChanged(0, 10);
    inOrder2.verify(listener2).onProgramCounterChanged(10, 11);
    inOrder2.verify(listener2).onProgramCounterChanged(11, 0);
  }
}
