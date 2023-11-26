package computer.args;

import computer.model.Memory;

public interface Argument {

	String getLabel();

	byte getValue(Memory mem);

}
