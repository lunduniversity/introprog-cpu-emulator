package model;

import args.Operand;
import instruction.InstructionFactory;

public class CPU {

  private AddressableStorage memory;
  private ProgramCounter pc;
  private InstructionFactory factory;

  public CPU(AddressableStorage memory, ProgramCounter pc, InstructionFactory factory) {
    this.memory = memory;
    this.pc = pc;
    this.factory = factory;
  }

  public void step() {
    if (pc.isHalted()) {
      throw new IllegalStateException("CPU is halted");
    }
    int value = memory.getValueAt(Operand.of(pc.next()));
    factory.createInstruction(value).execute(memory, pc);
  }

  public void run() {
    if (pc.isHalted()) {
      throw new IllegalStateException("CPU is halted");
    }
    while (!pc.isHalted()) {
      int value = memory.getValueAt(Operand.of(pc.next()));
      factory.createInstruction(value).execute(memory, pc);
    }
  }
}
