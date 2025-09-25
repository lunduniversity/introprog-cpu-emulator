package instruction;

import static instruction.Instruction.ADDR_TYPE_CONSTANT;
import static instruction.Instruction.ADDR_TYPE_INVALID;
import static instruction.Instruction.ADDR_TYPE_MEMORY;
import static instruction.Instruction.ADDR_TYPE_REGISTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Memory;
import model.ProgramCounter;
import model.Registry;

class CpyTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setUp() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
  }

  @Test
  void testCopy() {
    // Setup operand for copying from R1 to R2
    int operand = 0; // Operand is not
    int srcRegister = Registry.nameToIdx(Registry.REG_R0); // Source register index
    int destRegister = Registry.nameToIdx(Registry.REG_R1); // Destination register index
    int operatorValue = (srcRegister << 4) | destRegister;
    int value = 123; // Value to be copied

    // Simulate reading source and destination addresses (register indices)
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(operatorValue);

    // Simulate reading value from the source register
    when(mockRegistry.getValueAt(srcRegister)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination register
    verify(mockRegistry).getValueAt(srcRegister);
    verify(mockRegistry).setValueAt(destRegister, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyRegisterToRegister() {
    // Setup operand for copying from register to register (R1 to R2)
    int operand = (ADDR_TYPE_REGISTER << 2) | ADDR_TYPE_REGISTER;
    int srcRegister = Registry.nameToIdx(Registry.REG_R0); // Source register index
    int destRegister = Registry.nameToIdx(Registry.REG_R1); // Destination register index
    int value = 123; // Value to be copied

    // Simulate reading source and destination addresses (register indices)
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcRegister);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    // Simulate reading value from the source register
    when(mockRegistry.getValueAt(srcRegister)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination register
    verify(mockRegistry).getValueAt(srcRegister);
    verify(mockRegistry).setValueAt(destRegister, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyMemoryToRegister() {
    // Similar to previous test, but with operand encoding for memory to register
    // (10 to 01)
    int operand = (ADDR_TYPE_MEMORY << 2) | ADDR_TYPE_REGISTER;
    int memoryAddress = 16; // Example memory address
    int destRegister = Registry.nameToIdx(Registry.REG_R0); // Destination register index
    int value = 456; // Value at the memory address to be copied

    // Set up mock behavior
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(memoryAddress);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied from memory to the register
    verify(mockRegistry).setValueAt(destRegister, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyConstantToRegister() {
    // Similar to previous test, but with operand encoding for constant to register
    // (00 to 01)
    int operand = (ADDR_TYPE_CONSTANT << 2) | ADDR_TYPE_REGISTER;
    int destRegister = Registry.nameToIdx(Registry.REG_R0); // Destination register index
    int value = 89; // Constant value to be copied

    // Set up mock behavior
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(value);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied from memory to the register
    verify(mockRegistry).setValueAt(destRegister, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyRegisterToMemory() {
    // Setup operand for copying from register to memory (R1 to memory address)
    int operand = (ADDR_TYPE_REGISTER << 2) | ADDR_TYPE_MEMORY;
    int srcRegister = Registry.nameToIdx(Registry.REG_R0); // Source register index
    int memoryAddress = 10; // Destination memory address
    int value = 123; // Value to be copied

    // Simulate reading source register index and destination memory address
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcRegister);
    when(mockMemory.getValueAt(2)).thenReturn(memoryAddress);
    // Simulate reading value from the source register
    when(mockRegistry.getValueAt(srcRegister)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination memory address
    verify(mockMemory).setValueAt(memoryAddress, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyMemoryToMemory() {
    // Setup operand for copying from memory to memory (memory address to memory
    // address)
    int operand = (ADDR_TYPE_MEMORY << 2) | ADDR_TYPE_MEMORY;
    int srcMemoryAddress = 5; // Source memory address
    int destMemoryAddress = 15; // Destination memory address
    int value = 456; // Value to be copied

    // Simulate reading source and destination memory addresses
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcMemoryAddress);
    when(mockMemory.getValueAt(2)).thenReturn(destMemoryAddress);
    // Simulate reading value from the source memory address
    when(mockMemory.getValueAt(srcMemoryAddress)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination memory address
    verify(mockMemory).setValueAt(destMemoryAddress, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testCopyConstantToMemory() {
    // Setup operand for copying from constant to memory (constant value to memory
    // address)
    int operand = (ADDR_TYPE_CONSTANT << 2) | ADDR_TYPE_MEMORY;
    int memoryAddress = 20; // Destination memory address
    int value = 89; // Constant value to be copied

    // Simulate reading destination memory address
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(value);
    when(mockMemory.getValueAt(2)).thenReturn(memoryAddress);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the constant value is copied to the destination memory address
    verify(mockMemory).setValueAt(memoryAddress, value);
  }

  // @Test - No longer needed, since rework of Cpy class
  void testInvalidSourceTypeThrowsException() {
    // Setup operand with an invalid source type
    int operand = (ADDR_TYPE_INVALID << 2) | ADDR_TYPE_REGISTER; // Invalid source type (11)

    Cpy cpyInstruction = new Cpy(operand);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction.execute(mockMemory, mockRegistry, mockPC, null));
  }

  // @Test - No longer needed, since rework of Cpy class
  void testInvalidDestinationTypeThrowsException() {
    // Setup operand with an invalid destination type
    int operand1 = (ADDR_TYPE_CONSTANT << 2)
        | ADDR_TYPE_CONSTANT; // Invalid destination type (00, constant value)
    int operand2 = (ADDR_TYPE_CONSTANT << 2)
        | ADDR_TYPE_INVALID; // Invalid destination type (11, illegal value)

    Cpy cpyInstruction1 = new Cpy(operand1);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction1.execute(mockMemory, mockRegistry, mockPC, null));
    Cpy cpyInstruction2 = new Cpy(operand2);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction2.execute(mockMemory, mockRegistry, mockPC, null));
  }

  @Test
  void testPrettyPrint() {
    Cpy cpy = new Cpy(0); // Operand without move flag (i.e. copy)
    // Set the next memory cell to index registers R1 to R2
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1))
        .thenReturn(
            Registry.nameToIdx(Registry.REG_OP1) << 4 | Registry.nameToIdx(Registry.REG_OP2));
    String expected = String.format("%s (OP1 %s OP2)", InstructionFactory.INST_NAME_CPY, Instruction.RIGHT_ARROW_CHAR);
    assertEquals(expected, cpy.prettyPrint(mockMemory, mockRegistry, 0));
  }

  @Test
  void testPrettyPrintWithMoveFlag() {
    Cpy cpy = new Cpy(1); // Operand with move flag set (i.e. move)
    // Set the next memory cell to index registers R1 to R2
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1))
        .thenReturn(
            Registry.nameToIdx(Registry.REG_OP1) << 4 | Registry.nameToIdx(Registry.REG_OP2));
    String expected = String.format("%s (OP1 %s OP2)", InstructionFactory.INST_NAME_MOV, Instruction.RIGHT_ARROW_CHAR);
    assertEquals(expected, cpy.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
