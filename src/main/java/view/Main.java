package view;

import instruction.InstructionFactory;
import io.IOModule;
import javax.swing.SwingUtilities;
import model.ByteStorage;
import model.CPU;
import model.ProgramCounter;

public class Main {

  private static final int NUM_MEMORY_CELLS = 128;

  public static void main(String[] args) {
    // Create the model
    ByteStorage memory = new ByteStorage(NUM_MEMORY_CELLS);
    ProgramCounter pc = new ProgramCounter();
    IOModule io = new IOModule();
    CPU cpu = new CPU(memory, pc, new InstructionFactory(), io);

    SwingUtilities.invokeLater(() -> new ComputerUI(memory, pc, cpu, io));
  }
}
