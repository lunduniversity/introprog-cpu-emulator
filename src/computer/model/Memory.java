package computer.model;

import computer.args.Address;

public interface Memory {

	byte getValueAt(Address a);
	void setValueAt(Address a, byte value);

	int numCells();

	void addListener(MemoryListener listener);

}

