package model;

import java.util.ArrayList;
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
    notifyListeners(cleanedAddress, cleanedValue);
  }

  @Override
  public int getValueAt(int address) {
    return store[address & 0xFF];
  }

  @Override
  public int size() {
    return store.length;
  }

  public void reset() {
    for (int i = 0; i < store.length; i++) {
      store[i] = 0;
      notifyListeners(i, 0);
    }
  }

  @Override
  public void addListener(StorageListener listener) {
    listeners.add(listener);
  }

  private void notifyListeners(int address, int value) {
    for (StorageListener listener : listeners) {
      listener.onMemoryChanged(address, value);
    }
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
        chunks.stream().map(chunk -> encoder.encodeToString(chunk)).collect(Collectors.toList());

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
          notifyListeners(offset + j, store[offset + j]);
        }
        offset += decoded.length;
        i = end;
      }
    }
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
    for (int i = startIdx - 1; i < endIdx; i++) {
      notifyListeners(i, store[i]);
    }

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
    for (int i = startIdx; i <= endIdx; i++) {
      notifyListeners(i, store[i]);
    }

    return true;
  }
}
