package model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import instruction.Instruction;
import instruction.InstructionFactory;
import io.IO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CPUTest {

  private CPU cpu;
  private Memory memory;
  private ProgramCounter pc;
  private InstructionFactory factory;
  private IO io;

  @BeforeEach
  public void setUp() {
    memory = mock(Memory.class);
    pc = mock(ProgramCounter.class);
    factory = mock(InstructionFactory.class);
    io = mock(IO.class);
    cpu = new CPU(memory, pc, factory, io);
  }

  @Test
  public void testStepExecutesSingleInstruction() {
    when(pc.isHalted()).thenReturn(false);
    when(pc.next()).thenReturn(0, 1, 2, 3);
    when(memory.getValueAt(any(int.class)))
        .thenReturn(InstructionFactory.INST_ADD); // Example instruction
    Instruction mockInstruction = mock(Instruction.class);
    when(factory.createInstruction(InstructionFactory.INST_ADD)).thenReturn(mockInstruction);

    cpu.step();

    verify(mockInstruction, times(1)).execute(eq(memory), any(Registry.class), eq(pc), eq(io));
  }

  @Test
  public void testRunExecutesUntilHalted() {
    // Note: CPU does an initial check for halt before starting the loop
    when(pc.isHalted()).thenReturn(false, false, false, true); // Will halt after two steps
    when(memory.getValueAt(any(int.class)))
        .thenReturn(InstructionFactory.INST_ADD); // Example instruction
    Instruction mockInstruction = mock(Instruction.class);
    when(factory.createInstruction(InstructionFactory.INST_ADD)).thenReturn(mockInstruction);

    cpu.run();

    verify(mockInstruction, times(2)).execute(eq(memory), any(Registry.class), eq(pc), eq(io));
  }

  @Test
  public void testStepWithCPUHaltedThrowsException() {
    when(pc.isHalted()).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> cpu.step());
  }

  @Test
  public void testRunWithCPUHaltedThrowsException() {
    when(pc.isHalted()).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> cpu.run());
  }

  @Test
  public void testRunDetectsInfiniteLoop() {
    when(pc.isHalted())
        .thenReturn(false, false, false, false, false, false, false, false, false, false);
    when(pc.getCurrentIndex()).thenReturn(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    when(memory.getValueAt(any(int.class)))
        .thenReturn(InstructionFactory.INST_ADD); // Example instruction
    Instruction mockInstruction = mock(Instruction.class);
    when(factory.createInstruction(InstructionFactory.INST_ADD)).thenReturn(mockInstruction);

    assertThrows(IllegalStateException.class, () -> cpu.run());
  }

  @Test
  public void testResetResetsProgramCounterAndRegistry() {
    Registry reg = cpu.getRegistry();
    for (int i = 0; i < Registry.NUM_REGISTERS; i++) {
      reg.setValueAt(i, i + 1);
    }
    cpu.reset();
    verify(pc, times(1)).reset();
    for (int i = 0; i < Registry.NUM_REGISTERS; i++) {
      assert reg.getValueAt(i) == 0;
    }
  }

  @Test
  public void testAddRegistryListener() {
    StorageListener listener = mock(StorageListener.class);
    cpu.addRegistryListener(listener);
    cpu.getRegistry().setValueAt(5, 12);
    verify(listener, times(1)).onStorageChanged(5, null);
  }
}
