package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  public void setUp() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
  }

  @Test
  public void testMoveRegisterToRegister() {
    // Setup operand for copying from register to register (R1 to R2)
    int operand = (0b01 << 2) | 0b01;
    int srcRegister = Registry.nameToIdx("R1"); // Source register index
    int destRegister = Registry.nameToIdx("R2"); // Destination register index
    int value = 123; // Value to be copied

    // Simulate reading source and destination addresses (register indices)
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcRegister);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    // Simulate reading value from the source register
    when(mockRegistry.getRegister(srcRegister)).thenReturn(value);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination register
    verify(mockRegistry).getRegister(srcRegister);
    verify(mockRegistry).setRegister(destRegister, value);

    // Verify that the source is 0 after being moved
    verify(mockRegistry).setRegister(srcRegister, 0);
  }

  @Test
  public void testMoveMemoryToRegister() {
    // Similar to previous test, but with operand encoding for memory to register (10 to 01)
    int operand = (0b10 << 2) | 0b01;
    int memoryAddress = 16; // Example memory address
    int destRegister = Registry.nameToIdx("R1"); // Destination register index
    int value = 456; // Value at the memory address to be copied

    // Set up mock behavior
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(memoryAddress);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied from memory to the register
    verify(mockRegistry).setRegister(destRegister, value);

    // Verify that the source is 0 after being moved
    verify(mockMemory).setValueAt(memoryAddress, 0);
  }

  @Test
  public void testMoveConstantToRegister() {
    // Similar to previous test, but with operand encoding for constant to register (00 to 01)
    int operand = (0b00 << 2) | 0b01;
    int destRegister = Registry.nameToIdx("R1"); // Destination register index
    int value = 89; // Constant value to be copied

    // Set up mock behavior
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(value);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied from memory to the register
    verify(mockRegistry).setRegister(destRegister, value);

    // Verify that the source is 0 agter being moved
    verify(mockMemory).setValueAt(1, 0);
  }

  @Test
  public void testMoveRegisterToMemory() {
    // Setup operand for copying from register to memory (R1 to memory address)
    int operand = (0b01 << 2) | 0b10;
    int srcRegister = Registry.nameToIdx("R1"); // Source register index
    int memoryAddress = 10; // Destination memory address
    int value = 123; // Value to be copied

    // Simulate reading source register index and destination memory address
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcRegister);
    when(mockMemory.getValueAt(2)).thenReturn(memoryAddress);
    // Simulate reading value from the source register
    when(mockRegistry.getRegister(srcRegister)).thenReturn(value);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination memory address
    verify(mockMemory).setValueAt(memoryAddress, value);

    // Verify that the source is 0 agter being moved
    verify(mockRegistry).setRegister(srcRegister, 0);
  }

  @Test
  public void testMoveMemoryToMemory() {
    // Setup operand for copying from memory to memory (memory address to memory address)
    int operand = (0b10 << 2) | 0b10;
    int srcMemoryAddress = 5; // Source memory address
    int destMemoryAddress = 15; // Destination memory address
    int value = 456; // Value to be copied

    // Simulate reading source and destination memory addresses
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcMemoryAddress);
    when(mockMemory.getValueAt(2)).thenReturn(destMemoryAddress);
    // Simulate reading value from the source memory address
    when(mockMemory.getValueAt(srcMemoryAddress)).thenReturn(value);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination memory address
    verify(mockMemory).setValueAt(destMemoryAddress, value);

    // Verify that the source is 0 agter being moved
    verify(mockMemory).setValueAt(srcMemoryAddress, 0);
  }

  @Test
  public void testMoveConstantToMemory() {
    // Setup operand for copying from constant to memory (constant value to memory address)
    int operand = (0b00 << 2) | 0b10;
    int memoryAddress = 20; // Destination memory address
    int value = 89; // Constant value to be copied

    // Simulate reading destination memory address
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(value);
    when(mockMemory.getValueAt(2)).thenReturn(memoryAddress);

    Mov movInstruction = new Mov(operand);
    movInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the constant value is copied to the destination memory address
    verify(mockMemory).setValueAt(memoryAddress, value);

    // Verify that the source is 0 agter being moved
    verify(mockMemory).setValueAt(1, 0);
  }

  @Test
  public void testInvalidSourceTypeThrowsException() {
    // Setup operand with an invalid source type
    int operand = (0b11 << 2) | 0b01; // Invalid source type (11)

    Mov movInstruction = new Mov(operand);
    assertThrows(
        IllegalArgumentException.class,
        () -> movInstruction.execute(mockMemory, mockRegistry, mockPC, null));
  }

  @Test
  public void testInvalidDestinationTypeThrowsException() {
    // Setup operand with an invalid destination type
    int operand1 = (0b00 << 2) | 0b00; // Invalid destination type (00, constant value)
    int operand2 = (0b00 << 2) | 0b11; // Invalid destination type (11, illegal value)

    Mov movInstruction1 = new Mov(operand1);
    assertThrows(
        IllegalArgumentException.class,
        () -> movInstruction1.execute(mockMemory, mockRegistry, mockPC, null));
    Mov movInstruction2 = new Mov(operand2);
    assertThrows(
        IllegalArgumentException.class,
        () -> movInstruction2.execute(mockMemory, mockRegistry, mockPC, null));
  }

  @Test
  public void testToString() {
    // Test all possible operands
    int[] bits = {0b00, 0b01, 0b10, 0b11};
    for (int src : bits) {
      for (int dst : bits) {
        Mov mov = new Mov((src << 2) | dst);
        assertEquals(InstructionFactory.INST_NAME_MOV + " " + mov.printOperand(), mov.toString());
      }
    }
  }
}
