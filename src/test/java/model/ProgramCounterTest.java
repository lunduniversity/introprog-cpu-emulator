package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ProgramCounterTest {

  private ProgramCounter pc;
  private RegStorage registry;

  @BeforeEach
  void setUp() {
    registry = mock(RegStorage.class);
    pc = new ProgramCounter(registry, 256);
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
  void testHaltSetsHalted() {
    assertFalse(pc.isHalted(), "Initially, the program counter should not be halted.");
    pc.halt();
    assertTrue(pc.isHalted(), "After halting, the program counter should be halted.");
  }

  @Test
  void testIsHaltedReadsRawPC() {
    pc.isHalted();
    verify(registry).getRawValue(Registry.REG_PC);
  }

  @Test
  void testIsHaltedThrowsExceptionIfOutOfBounds() {
    when(registry.getRawValue(Registry.REG_PC)).thenReturn(256);
    assertThrows(IllegalStateException.class, () -> pc.isHalted());
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
