package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

class LdATest {
  @Test
  void testLoadFromAddressOperation() {
    Memory mockMemory = mock(Memory.class);
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Setup the operand, the intermediate address read from memory, and the final value
    int operand = 0; // The register to load the value into
    int intermediateAddress =
        2; // The address read from memory indicating where the actual value is stored
    int finalValue = 123; // The value to be loaded from the second memory address
    int memoryAddress =
        1; // The next address in the program counter, pointing to the intermediate address

    // Simulate the program counter pointing to the next memory address
    when(mockPC.next()).thenReturn(memoryAddress);

    // Simulate memory returning an address at the first read
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(intermediateAddress);

    // Simulate memory returning the actual value at the second read, from the intermediate address
    when(mockMemory.getValueAt(intermediateAddress)).thenReturn(finalValue);

    LdA ldaInstruction = new LdA(operand);
    ldaInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the registry's setRegister method is called with the correct value
    verify(mockRegistry).setValueAt(operand, finalValue);
  }

  @Test
  void testToString() {
    LdA loadOP1 = new LdA(Registry.nameToIdx(Registry.REG_OP1));
    LdA loadOP2 = new LdA(Registry.nameToIdx(Registry.REG_OP2));
    LdA loadR2 = new LdA(Registry.nameToIdx(Registry.REG_R2));
    assertEquals(InstructionFactory.INST_NAME_LDA + " (dst: OP1)", loadOP1.toString());
    assertEquals(InstructionFactory.INST_NAME_LDA + " (dst: OP2)", loadOP2.toString());
    assertEquals(InstructionFactory.INST_NAME_LDA + " (dst: R2)", loadR2.toString());
  }
}
