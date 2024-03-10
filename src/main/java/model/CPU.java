package model;

import args.Address;
import instruction.InstructionFactory;

public class CPU {

	private AddressableStorage memory;
	private ProgramCounter pc;

	public CPU(AddressableStorage memory, ProgramCounter pc) {
		this.memory = memory;
		this.pc = pc;
	}

	public void step() {
		if (pc.isHalted()) {
			throw new IllegalStateException("CPU is halted");
		}
		int value = memory.getValueAt(new Address(pc.next()));
		InstructionFactory.createInstruction(value).execute(memory, pc);
	}

	public void run() {
		if (pc.isHalted()) {
			throw new IllegalStateException("CPU is halted");
		}
		while (!pc.isHalted()) {
			int value = memory.getValueAt(new Address(pc.next()));
			InstructionFactory.createInstruction(value).execute(memory, pc);
		}
	}
}
