package computer.view;

import javax.swing.SwingUtilities;

import computer.model.CPU;
import computer.model.Memory;
import computer.model.ProgramCounter;

public class Main {

	private static final int NUM_MEMORY_CELLS = 20;
	private static final int NUM_REGISTER_CELLS = 3; // Excluding PC

	public static void main(String[] args) {
		// Create the model
		Memory mem = null;
		Memory reg = null;
		CPU cpu = null;
		ProgramCounter pc = null;

		SwingUtilities.invokeLater(() -> new ComputerUI(mem, reg, pc));
	}

}
