package computer.args;

import computer.model.Memory;

public class Address implements Argument {
	private String label;

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public byte getValue(Memory mem) {
		return mem.getValueAt(this);
	}

}
