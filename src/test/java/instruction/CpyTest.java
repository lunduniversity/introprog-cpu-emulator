package instruction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CpyTest {

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
  public void testCopyRegisterToRegister() {
    // Setup operand for copying from register to register (01 to 01)
    int operand = (0b01 << 2) | 0b01;
    int srcRegister = Registry.nameToIdx("R1"); // Source register index
    int destRegister = Registry.nameToIdx("R2"); // Destination register index
    int value = 123; // Value to be copied

    // Simulate reading source and destination addresses (register indices)
    when(mockPC.next()).thenReturn(1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(srcRegister);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    // Simulate reading value from the source register
    when(mockRegistry.getRegister(srcRegister)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction._execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied to the destination register
    verify(mockRegistry).getRegister(srcRegister);
    verify(mockRegistry).setRegister(destRegister, value);
  }

  @Test
  public void testCopyMemoryToRegister() {
    // Similar to previous test, but with operand encoding for memory to register (10 to 01)
    int operand = (0b10 << 2) | 0b01;
    int memoryAddress = 16; // Example memory address
    int destRegister = Registry.nameToIdx("R1"); // Destination register index
    int value = 456; // Value at the memory address to be copied

    // Set up mock behavior
    when(mockPC.next()).thenReturn(1, 2);
    when(mockMemory.getValueAt(1)).thenReturn(memoryAddress);
    when(mockMemory.getValueAt(2)).thenReturn(destRegister);
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    Cpy cpyInstruction = new Cpy(operand);
    cpyInstruction._execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the value is copied from memory to the register
    verify(mockRegistry).setRegister(destRegister, value);
  }

  @Test
  public void testInvalidSourceTypeThrowsException() {
    // Setup operand with an invalid source type
    int operand = (0b11 << 2) | 0b01; // Invalid source type (11)

    Cpy cpyInstruction = new Cpy(operand);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction._execute(mockMemory, mockRegistry, mockPC, null));
  }

  @Test
  public void testInvalidDestinationTypeThrowsException() {
    // Setup operand with an invalid destination type
    int operand1 = (0b00 << 2) | 0b00; // Invalid destination type (00, constant value)
    int operand2 = (0b00 << 2) | 0b11; // Invalid destination type (11, illegal value)

    Cpy cpyInstruction1 = new Cpy(operand1);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction1._execute(mockMemory, mockRegistry, mockPC, null));
    Cpy cpyInstruction2 = new Cpy(operand2);
    assertThrows(
        IllegalArgumentException.class,
        () -> cpyInstruction2._execute(mockMemory, mockRegistry, mockPC, null));
  }
}
