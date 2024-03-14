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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CPUTest {

  private CPU cpu;
  private Memory memory;
  private ProgramCounter pc;
  private InstructionFactory factory;

  @BeforeEach
  public void setUp() {
    memory = mock(Memory.class);
    pc = mock(ProgramCounter.class);
    factory = mock(InstructionFactory.class);
    cpu = new CPU(memory, pc, factory);
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

    verify(mockInstruction, times(1)).execute(eq(memory), any(Registry.class), eq(pc), null);
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

    verify(mockInstruction, times(2)).execute(eq(memory), any(Registry.class), eq(pc), null);
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
}
