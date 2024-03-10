package instruction;

import java.util.List;

import args.Argument;
import model.AddressableStorage;
import model.ProgramCounter;

public abstract class Instruction {

	protected final String name;
	protected final List<Argument> args;

	public Instruction(String name) {
		this.name = name;
		this.args = null;
	}

	public String getName() {
		return name;
	}

	public abstract void execute(AddressableStorage mem, ProgramCounter pc);

}
