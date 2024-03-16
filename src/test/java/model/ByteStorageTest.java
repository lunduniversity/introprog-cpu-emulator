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
  private ByteStorage bigstore;
  private int memorySize = 10;

  @BeforeEach
  void setUp() {
    store = new ByteStorage(memorySize);
    bigstore = new ByteStorage(128);
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

  @Test
  void testExportFilledStorageAsBase64() {
    int[] values = new int[bigstore.size()];
    for (int i = 0; i < bigstore.size(); i++) {
      values[i] = i;
      bigstore.setValueAt(i, (i + 1));
      System.out.println("Storing " + (i) + " at " + i);
    }

    String base64 = bigstore.exportAsBase64();
    String expectedBase64 =
        "AQIDBAUGBwgJCgsMDQ4PEBESExQVFhcYGRobHB0eHyAhIiMkJSYnKCkqKywtLi8wMTIzNDU2Nzg5Ojs8PT4/QEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaW1xdXl9gYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXp7fH1+f4A=";
    assertEquals(expectedBase64, base64);
  }

  @Test
  public void testBasicExportAsBase64() {
    // Set some example values
    bigstore.setValueAt(0, 64);
    bigstore.setValueAt(1, 32);
    bigstore.setValueAt(2, 19);
    bigstore.setValueAt(3, 2);
    bigstore.setValueAt(4, 127);

    String expected = "QCATAn8=";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  public void testExportWithInitialOffset() {
    // Leave some initial bytes empty
    for (int i = 7; i <= 29; i++) bigstore.setValueAt(i, (i + 1));

    String expected = ":7:CQoLDA0ODxAREhMUFRYXGBkaGxwdHg==";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  public void testExportWithSmallHoles() {
    // Set bytes in ranges 0-7, 8-14 and 16-36
    for (int i = 0; i <= 7; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 8; i <= 14; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 16; i <= 36; i++) bigstore.setValueAt(i, (i + 1));

    String expected = "AQIDBAUGBwgJCgsMDQ4P:90:ERITFBUWFxgZGhscHR4fICEiIyQl";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  public void testExportWithLargeHoles() {
    // Set bytes in ranges 0-12, 20-29 and 90-107
    for (int i = 0; i <= 12; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 20; i <= 29; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 90; i <= 107; i++) bigstore.setValueAt(i, (i + 1));

    String expected = "AQIDBAUGBwgJCgsMDQ==:6:FhcYGRobHB0e:59:XF1eX2BhYmNkZWZnaGlqa2w=";
    assertEquals(expected, bigstore.exportAsBase64());
  }
}
