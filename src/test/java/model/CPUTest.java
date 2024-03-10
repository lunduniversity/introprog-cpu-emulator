package model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import args.Address;
import instruction.Instruction;
import instruction.InstructionFactory;

public class CPUTest {

    private CPU cpu;
    private AddressableStorage memory;
    private ProgramCounter pc;

    @BeforeEach
    public void setUp() {
        memory = mock(AddressableStorage.class);
        pc = mock(ProgramCounter.class);
        cpu = new CPU(memory, pc);
    }

    @Test
    public void testStepExecutesSingleInstruction() {
        when(pc.isHalted()).thenReturn(false);
        when(memory.getValueAt(any(Address.class))).thenReturn(InstructionFactory.INST_ADD); // Example instruction
        Instruction mockInstruction = mock(Instruction.class);
        when(InstructionFactory.createInstruction(InstructionFactory.INST_ADD)).thenReturn(mockInstruction);

        cpu.step();

        verify(mockInstruction, times(1)).execute(memory, pc);
    }

    @Test
    public void testRunExecutesUntilHalted() {
        when(pc.isHalted()).thenReturn(false, false, true); // Will halt after two steps
        when(memory.getValueAt(any(Address.class))).thenReturn(InstructionFactory.INST_ADD); // Example instruction
        Instruction mockInstruction = mock(Instruction.class);
        when(InstructionFactory.createInstruction(InstructionFactory.INST_ADD)).thenReturn(mockInstruction);

        cpu.run();

        verify(mockInstruction, times(2)).execute(memory, pc);
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
