package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import instruction.Hlt;
import instruction.Instruction;
import instruction.InstructionFactory;
import instruction.Nop;
import io.IO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CPUTest {

  private CPU cpu;
  private Memory memory;
  private ProgramCounter pc;
  private InstructionFactory factory;
  private IO io;

  @BeforeEach
  void setUp() {
    memory = mock(Memory.class);
    when(memory.size()).thenReturn(256);
    factory = mock(InstructionFactory.class);
    io = mock(IO.class);
    cpu = new CPU(memory, factory, io);
    pc = cpu.getProgramCounter();
  }

  @Test
  void testStepExecutesSingleInstruction() {
    when(memory.getValueAt(any(int.class)))
        .thenReturn(InstructionFactory.INST_NOP); // Example instruction
    Instruction mockInstruction = mock(Instruction.class);
    when(factory.createInstruction(InstructionFactory.INST_NOP)).thenReturn(mockInstruction);

    cpu.step();

    verify(mockInstruction).execute(eq(memory), any(Registry.class), eq(pc), eq(io));
  }

  @Test
  void testRunExecutesUntilHalted() {
    // Note: CPU does an initial check for halt before starting the loop.
    // Return halt operation after three steps
    when(memory.getValueAt(any(int.class)))
        .thenReturn(
            InstructionFactory.INST_NOP,
            InstructionFactory.INST_NOP,
            InstructionFactory.INST_NOP,
            InstructionFactory.INST_HLT);
    Instruction nop = mock(Nop.class);
    Instruction halt = mock(Hlt.class);
    when(factory.createInstruction(InstructionFactory.INST_NOP)).thenReturn(nop);
    when(factory.createInstruction(InstructionFactory.INST_HLT)).thenReturn(halt, new Hlt(0));

    cpu.run();

    verify(nop, times(3)).execute(eq(memory), any(Registry.class), eq(pc), eq(io));
    verify(halt).execute(eq(memory), any(Registry.class), eq(pc), eq(io));
  }

  @Test
  void testStepWithCPUHaltedThrowsException() {
    pc.halt();
    assertThrows(IllegalStateException.class, () -> cpu.step());
  }

  @Test
  void testRunWithCPUHaltedThrowsException() {
    pc.halt();
    assertThrows(IllegalStateException.class, () -> cpu.run());
  }

  @Test
  void testRunDetectsInfiniteLoop() {
    when(memory.getValueAt(any(int.class)))
        .thenReturn(InstructionFactory.INST_NOP); // Example instruction
    Instruction nop = mock(Nop.class);
    when(factory.createInstruction(InstructionFactory.INST_NOP)).thenReturn(nop);

    assertThrows(IllegalStateException.class, () -> cpu.run());
  }

  @Test
  void testResetResetsProgramCounterAndRegistry() {
    Registry reg = cpu.getRegistry();
    for (int i = 0; i < Registry.NUM_REGISTERS; i++) {
      reg.setValueAt(i, i + 1);
    }
    assertEquals(Registry.NUM_REGISTERS, pc.getCurrentIndex());
    cpu.reset();
    assertEquals(0, pc.getCurrentIndex());
    for (int i = 0; i < Registry.NUM_REGISTERS; i++) {
      assert reg.getValueAt(i) == 0;
    }
  }

  @Test
  void testAddRegistryListener() {
    StorageListener listener = mock(StorageListener.class);
    cpu.addRegistryListener(listener);
    cpu.getRegistry().setValueAt(5, 12);
    verify(listener, times(1)).onStorageChanged(5, new int[] {12});
  }
}
