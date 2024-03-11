package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import args.Operand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ByteStorageTest {

  private ByteStorage memory;
  private final int memorySize = 10;

  @BeforeEach
  void setUp() {
    memory = new ByteStorage(memorySize);
  }

  @Test
  void testSetValueAtAndGetValues() {
    int addressValue = 5;
    int intValue = 12;
    Operand address = Operand.mem(addressValue);
    Operand value = Operand.of(intValue);
    memory.setValueAt(address, value);

    int storedValue = memory.getValueAt(address);
    assertEquals(intValue, storedValue, "The value retrieved should match the value set.");
  }

  @Test
  void testMemorySize() {
    assertEquals(
        memorySize, memory.size(), "Memory size should match the size provided at initialization.");
  }

  @Test
  void testNotifyListenersOnValueSet() {
    StorageListener mockListener = mock(StorageListener.class);
    Operand address = Operand.of((int) 3);
    Operand value = Operand.of((int) 100);

    memory.addListener(mockListener);
    memory.setValueAt(address, value);

    verify(mockListener, times(1)).onMemoryChanged(eq(address), eq((int) 100));
  }

  @Test
  void testAddListener() {
    StorageListener listener1 = mock(StorageListener.class);
    StorageListener listener2 = mock(StorageListener.class);
    memory.addListener(listener1);
    memory.addListener(listener2);

    Operand address = Operand.of((int) 1);
    Operand value = Operand.of((int) 50);
    memory.setValueAt(address, value);

    verify(listener1, times(1)).onMemoryChanged(eq(address), eq((int) 50));
    verify(listener2, times(1)).onMemoryChanged(eq(address), eq((int) 50));
  }
}
