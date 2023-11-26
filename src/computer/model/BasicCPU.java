package computer.model;

import java.util.HashMap;
import java.util.Map;

public class BasicCPU implements CPU {

	private Memory memory;
	private ProgramCounter pc;

	public BasicCPU(Memory memory, ProgramCounter pc) {
		this.memory = memory;
		this.pc = pc;
	}

	@Override
	public void step(Memory memory, ProgramCounter pc) {

	}

	@Override
	public Map<String, Byte> getInstructions() {
		HashMap<String,Byte> map = new HashMap<>();
		map.put("ADD", (byte) 0x01); // Addition
		map.put("SUB", (byte) 0x02); // Subtraction
		map.put("MOV", (byte) 0x03); // Copy value from one cell to another
		map.put("LD",  (byte) 0x04); // Load into register
		map.put("ST",  (byte) 0x05); // Store register value in memory
		map.put("JMP", (byte) 0x06); // Jump to address
		map.put("JE",  (byte) 0x07); // Jump if equal
		map.put("JNE", (byte) 0x08); // Jump if not equal
		map.put("HLT", (byte) 0x09); // Halt

		return map;
	}



}
