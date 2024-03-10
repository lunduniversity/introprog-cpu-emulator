package model;

import java.util.HashSet;
import java.util.Set;

import args.Address;
import args.Argument;

public class ByteStorage implements ObservableStorage {

    private int[] memory;
    private Set<StorageListener> listeners = new HashSet<>();

    public ByteStorage(int size) {
        memory = new int[size];
    }

    public void setValueAt(Address address, Argument value) {
        memory[address.getAddress()] = value.getValue(this);
        notifyListeners(address, value.getValue(this));
    }

    public int getValueAt(Address address) {
        return memory[address.getAddress()];
    }

    public int size() {
        return memory.length;
    }

    public void addListener(StorageListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(Address address, int value) {
        for (StorageListener listener : listeners) {
            listener.onMemoryChanged(address, value);
        }
    }

}
