package view;

import javax.swing.SwingUtilities;

import model.Computer;

public class Main {

	private static final int NUM_MEMORY_CELLS = 20;
	private static final int NUM_REGISTER_CELLS = 3; // Excluding PC

	public static void main(String[] args) {
		// Create the model
		Computer computer = new Computer(NUM_MEMORY_CELLS, NUM_REGISTER_CELLS);

		SwingUtilities.invokeLater(() -> new ComputerUI(computer));
	}

}
