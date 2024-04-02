package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    StorageListener listener1 = mock(StorageListener.class);
    StorageListener listener2 = mock(StorageListener.class);
    store.addListener(listener1);
    store.addListener(listener2);

    int address = 1;
    int value = 50;
    store.setValueAt(address, value);

    verify(listener1, times(1)).onStorageChanged(eq(address), eq(new int[] {value}));
    verify(listener2, times(1)).onStorageChanged(eq(address), eq(new int[] {value}));
  }

  @Test
  void testReset() {
    StorageListener listener = mock(StorageListener.class);
    store.addListener(listener);

    store.setValueAt(0, 100);
    store.setValueAt(1, 125);
    store.setValueAt(2, 150);
    store.reset();

    verify(listener).onStorageChanged(eq(0), eq(new int[] {100}));
    verify(listener).onStorageChanged(eq(1), eq(new int[] {125}));
    verify(listener).onStorageChanged(eq(2), eq(new int[] {150}));
    verify(listener).onStorageChanged(eq(0), eq(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    assertEquals(0, store.getValueAt(0));
    assertEquals(0, store.getValueAt(1));
    assertEquals(0, store.getValueAt(2));
  }

  @Test
  void testExportEmptyStorageAsBase64() {
    String base64 = store.exportAsBase64();
    assertEquals("", base64);
  }

  @Test
  void testExportFilledStorageAsBase64() {
    int[] values = new int[bigstore.size()];
    for (int i = 0; i < bigstore.size(); i++) {
      values[i] = i;
      bigstore.setValueAt(i, (i + 1));
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

    String expected = ":7:CAkKCwwNDg8QERITFBUWFxgZGhscHR4=";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  public void testExportWithSmallHoles() {
    // Set bytes in ranges 0-6, 8-13 and 16-35, leaving holes at 7, 14 and 15
    for (int i = 0; i < 7; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 8; i < 14; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 16; i < 36; i++) bigstore.setValueAt(i, (i + 1));

    String expected = "AQIDBAUGBw==:1:CQoLDA0O:2:ERITFBUWFxgZGhscHR4fICEiIyQ=";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  public void testExportWithLargeHoles() {
    // Set bytes in ranges 0-11, 20-28 and 90-106, leaving holes at 12-19 and 29-89
    for (int i = 0; i < 12; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 20; i < 29; i++) bigstore.setValueAt(i, (i + 1));
    for (int i = 90; i < 107; i++) bigstore.setValueAt(i, (i + 1));

    String expected = "AQIDBAUGBwgJCgsM:8:FRYXGBkaGxwd:61:W1xdXl9gYWJjZGVmZ2hpams=";
    assertEquals(expected, bigstore.exportAsBase64());
  }

  @Test
  void testImportInvalidFormatThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> bigstore.importFromBase64(":45:invalid format (contains space):3:"));
  }

  @Test
  void testImportBasicBase64() {
    // Corresponds to the bytes set in testBasicExportAsBase64
    String base64 = "QCATAn8=";

    bigstore.importFromBase64(base64);
    assertEquals(64, bigstore.getValueAt(0));
    assertEquals(32, bigstore.getValueAt(1));
    assertEquals(19, bigstore.getValueAt(2));
    assertEquals(2, bigstore.getValueAt(3));
    assertEquals(127, bigstore.getValueAt(4));
  }

  @Test
  void testImportWithInitialOffset() {
    // From testExportWithInitialOffset
    String encoded = ":7:CAkKCwwNDg8QERITFBUWFxgZGhscHR4=";

    bigstore.importFromBase64(encoded);
    for (int i = 0; i < 7; i++) assertEquals(0, bigstore.getValueAt(i));
    for (int i = 7; i < 29; i++) assertEquals(i + 1, bigstore.getValueAt(i));
  }

  @Test
  void testImportWithSmallHoles() {
    // From testExportWithSmallHoles
    String encoded = "AQIDBAUGBw==:1:CQoLDA0O:2:ERITFBUWFxgZGhscHR4fICEiIyQ=";

    bigstore.importFromBase64(encoded);
    // Verify bytes before the hole
    for (int i = 0; i < 7; i++) assertEquals(i + 1, bigstore.getValueAt(i));
    // Verify the first hole, at position 7
    assertEquals(0, bigstore.getValueAt(7));
    // Verify bytes between the holes
    for (int i = 8; i < 14; i++) assertEquals(i + 1, bigstore.getValueAt(i));
    // Verify the second hole, at positions 14-15
    for (int i = 14; i < 16; i++) assertEquals(0, bigstore.getValueAt(i));
    // Verify bytes after the hole
    for (int i = 16; i < 36; i++) assertEquals(i + 1, bigstore.getValueAt(i));
  }

  @Test
  void testImportWithLargeHoles() {
    // From testExportWithLargeHoles
    String encoded = "AQIDBAUGBwgJCgsM:8:FRYXGBkaGxwd:61:W1xdXl9gYWJjZGVmZ2hpams=";

    bigstore.importFromBase64(encoded);
    // Verify initial range
    for (int i = 0; i < 12; i++) assertEquals(i + 1, bigstore.getValueAt(i));
    // Verify first large hole
    for (int i = 13; i < 19; i++) assertEquals(0, bigstore.getValueAt(i));
    // Verify second range
    for (int i = 20; i < 29; i++) assertEquals(i + 1, bigstore.getValueAt(i));
    // Verify second large hole
    for (int i = 30; i < 89; i++) assertEquals(0, bigstore.getValueAt(i));
    // Verify third range
    for (int i = 90; i < 107; i++) assertEquals(i + 1, bigstore.getValueAt(i));
  }
}
