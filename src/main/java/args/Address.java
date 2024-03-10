package args;

import model.ObservableStorage;

/**
 * Represents an address in the memory.
 * 
 * Note: Internally all addresses and values are stored as int, but the emulator
 * uses 8-bit values. Addresses make use of the first bit to address either
 * memory (0) or registers (1). Thus, the maximum addressable memory is 128.
 */
public class Address implements Argument {
	private int address;

	public static Address of(int address) {
		return new Address(address);
	}

	public static Address mem(int address) {
		return new Address(address);
	}

	public static Address reg(int address) {
		// Do bitwise OR to set the first bit to 1
		return new Address(address | 0x80);
	}

	public Address(int address) {
		this.address = address;
	}

	public String getLabel() {
		return String.format("0x%02X", address);
	}

	@Override
	public int getValue(ObservableStorage mem) {
		return mem.getValueAt(this);
	}

	public boolean isRegister() {
		// First bit indicates if it's a register (1) or memory (0)
		return (address & 0x80) == 0x80;
	}

	public int getAddress() {
		// Remove the first bit to get the actual address
		return address & 0x7F;
	}

	public int getRawAddress() {
		return address;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		Address other = (Address) obj;
		return other.address == this.address;
	}

	@Override
	public int hashCode() {
		return address;
	}

}
