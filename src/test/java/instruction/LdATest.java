package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LdATest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
    when(mockMemory.size()).thenReturn(50);
  }

  @Test
  void testLoadFromAddressOperation() {
    // Setup the operand, containing the source and destination registers
    int srcReg = Registry.nameToIdx(Registry.REG_RES);
    int destReg = Registry.nameToIdx(Registry.REG_R1);
    int operand = (srcReg << 4) | destReg;

    int value = 123; // The value to be loaded from memory
    int memoryAddress = 33; // The address in memory where value is stored

    // Simulate the program counter pointing to the next memory address
    when(mockPC.next()).thenReturn(1);

    // Simulate memory returning the operand on the first read
    when(mockMemory.getValueAt(1)).thenReturn(operand);

    // Simulate registry returning the memory address from srcReg
    when(mockRegistry.getValueAt(srcReg)).thenReturn(memoryAddress);

    // Simulate memory returning the value from the memory address
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    LdA ldaInstruction = new LdA(operand);
    ldaInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the registry's setRegister method is called with the correct value
    verify(mockRegistry).setValueAt(destReg, value);
  }

  @Test
  void testPrettyPrint() {
    // Setup the operand, containing the source and destination registers
    int srcReg = Registry.nameToIdx(Registry.REG_RES);
    int destReg = Registry.nameToIdx(Registry.REG_R1);
    int operand = (srcReg << 4) | destReg;

    when(mockMemory.getValueAt(1)).thenReturn(operand);

    LdA load = new LdA(0);
    assertEquals(
        InstructionFactory.INST_NAME_LDA + " (*RES " + Instruction.RIGHT_ARROW_CHAR + " R1)",
        load.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
