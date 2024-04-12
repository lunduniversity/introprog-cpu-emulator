package model;

import instruction.Instruction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import util.Range;

public class ByteStorage implements Memory {

  private int[] store;
  private Set<StorageListener> listeners = new HashSet<>();

  public ByteStorage(int size) {
    if (size <= 0 || size > 256) {
      throw new IllegalArgumentException("Invalid size: " + size);
    }
    store = new int[size];
  }

  @Override
  public void setValueAt(int address, int value) {
    if (address < 0 || address >= store.length) {
      throw new IllegalArgumentException("Address out of bounds: " + address);
    }
    store[address] = value;
    notifyListenersSingle(address, value);
  }

  @Override
  public int setValuesInRange(Range range, int[] values) {
    Range limited = range.limit(0, store.length);
    int length = Math.min(values.length, limited.length());

    System.arraycopy(values, 0, store, limited.from(), length);

    int[] usedValues = Arrays.copyOfRange(values, 0, length);
    int excess = values.length - length;

    notifyListenersRange(range.from(), usedValues);

    return excess;
  }

  @Override
  public int getValueAt(int address) {
    return store[address & 0xFF];
  }

  public int getRawValueAt(int address) {
    return store[address];
  }

  @Override
  public int[] getValuesInRange(Range range) {
    if (!(range.isAbove(0) && range.isBelow(store.length))) {
      throw new IllegalArgumentException("Invalid range: " + range);
    }
    return Arrays.copyOfRange(store, range.from(), range.to());
  }

  @Override
  public int size() {
    return store.length;
  }

  public void reset() {
    Arrays.fill(store, 0);
    notifyListenersAll();
  }

  @Override
  public void addListener(StorageListener listener) {
    listeners.add(listener);
  }

  private void notifyListenersSingle(int address, int value) {
    for (StorageListener listener : listeners) {
      listener.onStorageChanged(address, new int[] {value});
    }
  }

  private void notifyListenersRange(int startIdx, int[] values) {
    for (StorageListener listener : listeners) {
      listener.onStorageChanged(startIdx, values);
    }
  }

  private void notifyListenersAll() {
    notifyListenersRange(0, Arrays.copyOf(store, store.length));
  }

  @Override
  public String[] exportAsBinary() {
    // trim empty bytes from the end
    int end = store.length;
    while (end > 0 && store[end - 1] == 0) {
      end--;
    }
    if (end == 0) {
      return new String[0];
    }
    String[] data = new String[end];
    for (int i = 0; i < end; i++) {
      data[i] = Instruction.toBinaryString(store[i], 8, 4);
    }
    return data;
  }

  @Override
  public void importFromBinary(String[] data) {
    for (int i = 0; i < data.length && i < store.length; i++) {
      store[i] = Integer.parseInt(data[i], 2) & 0xFF;
    }
    // Fill the rest with zeros
    Arrays.fill(store, data.length, store.length, 0);
    notifyListenersAll();
  }

  public String exportAsBase64() {
    List<Integer> zeroCounts = new ArrayList<>();
    List<byte[]> chunks = new ArrayList<>();
    int i = 0;

    while (i < store.length) {
      if (store[i] == 0) {
        i += countZeros(i, zeroCounts);
      } else {
        chunks.add(copyNonZeroBytes(i));
        i += chunks.get(chunks.size() - 1).length;
      }
    }

    return formatBase64(zeroCounts, chunks);
  }

  private int countZeros(int startIndex, List<Integer> zeroCounts) {
    int count = 0;
    while (startIndex + count < store.length && store[startIndex + count] == 0) {
      count++;
    }
    zeroCounts.add(count);
    return count;
  }

  private byte[] copyNonZeroBytes(int startIndex) {
    int count = 0;
    while (startIndex + count < store.length && store[startIndex + count] != 0) {
      count++;
    }
    byte[] chunk = new byte[count];
    for (int j = 0; j < count; j++) {
      chunk[j] = (byte) store[startIndex + j];
    }
    return chunk;
  }

  private String formatBase64(List<Integer> zeroCounts, List<byte[]> chunks) {
    if (chunks.isEmpty()) {
      return "";
    }

    Encoder encoder = Base64.getEncoder();
    List<String> base64List =
        chunks.stream().map(encoder::encodeToString).collect(Collectors.toList());

    StringBuilder base64 = new StringBuilder();
    Iterator<Integer> zeroIt = zeroCounts.iterator();
    Iterator<String> base64It = base64List.iterator();

    // Initial zeros
    if (store[0] == 0 && zeroIt.hasNext()) {
      base64.append(":").append(zeroIt.next()).append(":");
    }

    // Build the encoded string
    while (base64It.hasNext()) {
      base64.append(base64It.next());
      if (zeroIt.hasNext()) {
        base64.append(":").append(zeroIt.next()).append(":");
      }
    }

    return base64.toString();
  }

  public void importFromBase64(String base64) {
    if (!base64.matches("(:\\d+:)?([A-Za-z0-9+/]+={0,2}:\\d+:)*[A-Za-z0-9+/]+={0,2}")) {
      throw new IllegalArgumentException("Invalid format!");
    }

    Decoder decoder = Base64.getDecoder();
    int offset = 0;
    int i = 0;

    while (i < base64.length()) {
      if (base64.charAt(i) == ':') {
        offset += parseEmptySpaces(base64, i);
        i = base64.indexOf(":", i + 1) + 1; // Move to the character after the second ':'
      } else {
        i = decodeAndStore(base64, i, offset, decoder);
      }
    }

    notifyListenersAll();
  }

  private int parseEmptySpaces(String base64, int start) {
    int end = base64.indexOf(":", start + 1);
    return Integer.parseInt(base64.substring(start + 1, end));
  }

  private int decodeAndStore(String base64, int start, int offset, Decoder decoder) {
    int end = base64.indexOf(":", start);
    if (end == -1) end = base64.length();
    String chunk = base64.substring(start, end);
    byte[] decoded = decoder.decode(chunk);
    System.arraycopy(decoded, 0, store, offset, decoded.length);
    return end;
  }

  @Override
  public boolean moveCellsUp(final int startIdx, final int endIdx) {
    // If startIdx is 0 do nothing
    if (startIdx <= 0 || endIdx > store.length || startIdx >= endIdx) {
      return false;
    }

    // Temporary store for the element that will be overwritten
    int tmp = store[startIdx - 1];

    // Move section up by one
    // Parameters are (src, srcPos, dest, destPos, length)
    System.arraycopy(store, startIdx, store, startIdx - 1, endIdx - startIdx);

    // Put the temp element at endIdx
    store[endIdx - 1] = tmp;

    // Notify listeners for each moved cell
    notifyListenersRange(startIdx - 1, Arrays.copyOfRange(store, startIdx - 1, endIdx));

    return true;
  }

  @Override
  public boolean moveCellsDown(final int startIdx, final int endIdx) {
    // Adjust for exclusive endIdx: check bounds
    if (endIdx >= store.length || startIdx < 0 || startIdx >= endIdx) {
      return false;
    }

    // Temporary store for the element that will be overwritten
    int temp = store[endIdx];

    // Move section down with System.arraycopy
    // Parameters are (src, srcPos, dest, destPos, length)
    System.arraycopy(store, startIdx, store, startIdx + 1, endIdx - startIdx);

    // Put the temp element at startIdx
    store[startIdx] = temp;

    // Notify listeners for each moved cell, including the swapped temp
    notifyListenersRange(startIdx, Arrays.copyOfRange(store, startIdx, endIdx + 1));

    return true;
  }

  @Override
  public void deleteCells(int startIdx, int endIdx) {
    // Check if the range is valid
    if (startIdx < 0 || endIdx >= store.length || startIdx > endIdx) {
      return;
    }

    int numberOfElementsToDelete = endIdx - startIdx + 1;
    int elementsToMove = store.length - endIdx - 1;

    // Move elements up
    System.arraycopy(store, endIdx + 1, store, startIdx, elementsToMove);

    // Fill vacated slots with zeros
    Arrays.fill(store, store.length - numberOfElementsToDelete, store.length, 0);

    // Notify listeners for each changed cell
    notifyListenersRange(startIdx, Arrays.copyOfRange(store, startIdx, store.length));
  }
}
