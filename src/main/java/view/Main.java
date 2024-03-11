package view;

import javax.swing.SwingUtilities;
import model.Computer;

public class Main {

  private static final int NUM_MEMORY_CELLS = 20;

  public static void main(String[] args) {
    // Create the model
    Computer computer = new Computer(NUM_MEMORY_CELLS);

    SwingUtilities.invokeLater(() -> new ComputerUI(computer));
  }
}
