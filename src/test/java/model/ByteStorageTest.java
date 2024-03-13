package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ByteStorageTest {

  private ByteStorage store;
  private final int memorySize = 10;

  @BeforeEach
  void setUp() {
    store = new ByteStorage(memorySize);
  }

  @Test
  void testSetValueAtAndGetValues() {
    int address = 5;
    int value = 12;
    store.setValueAt(address, value);

    int storedValue = store.getValueAt(address);
    assertEquals(value, storedValue, "The value retrieved should match the value set.");
  }

  @Test
  void testSetAndGetNegativeValue() {
    // The storage should truncate the value to 0-255 range, i.e. use only the last 8 bits.
    // Since Java int uses 2's complement, -12 is represented as 1...1 1111 0100.
    int address = 5;
    int value = -12;
    int expectedValue = 0b1111_0100; // 244
    store.setValueAt(address, value);

    int storedValue = store.getValueAt(address);
    assertEquals(expectedValue, storedValue, "The value retrieved should match the value set.");
  }

  @Test
  void testMemorySize() {
    assertEquals(
        memorySize, store.size(), "Memory size should match the size provided at initialization.");
  }

  @Test
  void testNotifyListenersOnValueSet() {
    StorageListener mockListener = mock(StorageListener.class);
    int address = 3;
    int value = 100;

    store.addListener(mockListener);
    store.setValueAt(address, value);

    verify(mockListener, times(1)).onMemoryChanged(eq(address), eq((int) 100));
  }

  @Test
  void testAddListener() {
    StorageListener listener1 = mock(StorageListener.class);
    StorageListener listener2 = mock(StorageListener.class);
    store.addListener(listener1);
    store.addListener(listener2);

    int address = 1;
    int value = 50;
    store.setValueAt(address, value);

    verify(listener1, times(1)).onMemoryChanged(eq(address), eq(value));
    verify(listener2, times(1)).onMemoryChanged(eq(address), eq(value));
  }
}
