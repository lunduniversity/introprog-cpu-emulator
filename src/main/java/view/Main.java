package view;

import instruction.InstructionFactory;
import io.IOModule;
import javax.swing.SwingUtilities;
import model.ByteStorage;
import model.CPU;
import util.LookAndFeelUtils;
import util.ThreadConfinementChecker;

public class Main {

  private static final int NUM_MEMORY_CELLS = 256; // 256 is the maximum number of memory cells!

  public static void main(String[] args) {
    // Install custom RepaintManager to detect Swing threading issues
    ThreadConfinementChecker.install();
    LookAndFeelUtils.setPlatformSpecificLookAndFeel();

    // Create the model
    ByteStorage memory = new ByteStorage(NUM_MEMORY_CELLS);
    IOModule io = new IOModule();
    CPU cpu = new CPU(memory, new InstructionFactory(), io);

    SwingUtilities.invokeLater(() -> new ComputerUI(memory, cpu, io));
  }
}
