package computer.instructions;

import java.util.List;

import computer.args.Argument;

public abstract class Instruction {

	protected final String name;
	protected final List<Argument> args;

	public Instruction(String name) {
		this.name = name;
		this.args = null;
	}

}
