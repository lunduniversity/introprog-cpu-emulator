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
    int op1 = Registry.nameToIdx(Registry.REG_R1);
    int op2 = Registry.nameToIdx(Registry.REG_RES);
    int operand = (op1 << 4) | op2;
    int value = 123; // The value to be loaded from memory
    int memoryAddress = 33; // The address in memory where value is stored

    // Simulate the program counter pointing to the next memory address
    when(mockPC.next()).thenReturn(1);

    // Simulate fetching the operand from memory
    when(mockMemory.getValueAt(1)).thenReturn(operand);

    // Simulate fetching the value from the register specified by the operand
    when(mockRegistry.getValueAt(op1)).thenReturn(memoryAddress);
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    LdA ldaInstruction = new LdA(0);
    ldaInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the registry's setRegister method is called with the correct value
    verify(mockRegistry).setValueAt(op2, value);
  }

  @Test
  void testPrettyPrint() {
    LdA load = new LdA(0);
    int leftOp = Registry.nameToIdx(Registry.REG_R2);
    int rightOp = Registry.nameToIdx(Registry.REG_OP1);
    int operand = (leftOp << 4) | rightOp;
    when(mockMemory.getValueAt(1)).thenReturn(operand);
    assertEquals(
        InstructionFactory.INST_NAME_LDA + " (*R2 " + Instruction.RIGHT_ARROW_CHAR + " OP1)",
        load.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
