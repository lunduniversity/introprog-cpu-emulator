package computer.model;

import java.util.Map;

public interface CPU {

	void step(Memory memory, ProgramCounter pc);

	Map<String, Byte> getInstructions();

}