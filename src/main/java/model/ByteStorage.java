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

public class ByteStorage implements Memory {

  private int[] store;
  private Set<StorageListener> listeners = new HashSet<>();

  public ByteStorage(int size) {
    store = new int[size & 0xFF];
  }

  @Override
  public void setValueAt(int address, int value) {
    int cleanedAddress = address & 0xFF;
    int cleanedValue = value & 0xFF;
    store[cleanedAddress] = cleanedValue;
    notifyListenersSingle(cleanedAddress, cleanedValue);
  }

  @Override
  public int setRange(int startIdx, int[] values) {
    int endIdx = Math.min(store.length, startIdx + values.length);
    int excess = Math.max(0, startIdx + values.length - store.length);
    for (int i = 0; startIdx + i < endIdx; i++) {
      store[startIdx + i] = values[i] & 0xFF;
    }
    notifyListenersRange(startIdx, Arrays.copyOfRange(store, startIdx, endIdx));

    return excess;
  }

  @Override
  public int getValueAt(int address) {
    return store[address & 0xFF];
  }

  @Override
  public int[] getRange(int startIdx, int endIdx) {
    return Arrays.copyOfRange(store, startIdx, startIdx + endIdx);
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
    List<Integer> countEmptBytes = new ArrayList<>();
    List<byte[]> chunks = new ArrayList<>();
    for (int i = 0; i < store.length; ) {
      int count = 0;
      while (i < store.length && store[i] == 0) {
        count++;
        i++;
      }
      if (count > 0) {
        countEmptBytes.add(count);
      } else {
        while (i < store.length && store[i] != 0) {
          count++;
          i++;
        }
        byte[] chunk = new byte[count];
        for (int j = 0; j < count; j++) {
          chunk[j] = (byte) store[i - count + j];
        }
        chunks.add(chunk);
      }
    }

    if (chunks.isEmpty()) {
      return "";
    }

    Encoder encoder = Base64.getEncoder();
    List<String> base64List =
        chunks.stream().map(encoder::encodeToString).collect(Collectors.toList());

    StringBuilder base64 = new StringBuilder();
    Iterator<Integer> spaceIt = countEmptBytes.iterator();
    Iterator<String> base64It = base64List.iterator();
    if (store[0] == 0) {
      base64.append(":").append(spaceIt.next()).append(":");
    }
    while (base64It.hasNext()) {
      base64.append(base64It.next());
      if (base64It.hasNext() && spaceIt.hasNext())
        base64.append(":").append(spaceIt.next()).append(":");
    }

    return base64.toString();
  }

  public void importFromBase64(String base64) {
    // Check with regex that input has correct format
    if (!base64.matches("(:\\d+:)?([A-Za-z0-9+/]+={0,2}:\\d+:)*[A-Za-z0-9+/]+={0,2}")) {
      throw new IllegalArgumentException("Invalid format!");
    }
    Decoder decoder = Base64.getDecoder();
    int offset = 0;
    for (int i = 0; i < base64.length(); ) {
      if (base64.charAt(i) == ':') {
        // Read the number of empty bytes
        int start = i + 1;
        int end = base64.indexOf(":", start + 1);
        int count = Integer.parseInt(base64.substring(start, end));
        i = end + 1;
        offset += count;
      } else {
        // Read the base64 encoded chunk and fill in the store
        int start = i;
        int end = base64.indexOf(":", start + 1);
        if (end == -1) {
          end = base64.length();
        }
        String chunk = base64.substring(start, end);
        byte[] decoded = decoder.decode(chunk);
        for (int j = 0; j < decoded.length; j++) {
          store[offset + j] = decoded[j] & 0xFF;
        }
        offset += decoded.length;
        i = end;
      }
    }
    notifyListenersAll();
  }

  @Override
  public boolean moveCellsUp(final int startIdx, final int endIdx) {
    // If startIdx is 0 do nothing
    if (startIdx <= 0) {
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
