package model;

import args.Address;

public interface StorageListener {

	void onMemoryChanged(Address address, int value);

}
