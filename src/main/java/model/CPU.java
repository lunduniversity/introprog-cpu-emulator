package model;

import instruction.InstructionFactory;
import io.IO;

public class CPU {

  public static final int REGISTERS_COUNT = 6;

  private Memory memory;
  private ProgramCounter pc;
  private InstructionFactory factory;
  private IO io;

  private RegStorage registry;

  public CPU(Memory memory, ProgramCounter pc, InstructionFactory factory, IO io) {
    this.memory = memory;
    this.pc = pc;
    this.factory = factory;
    this.io = io;
    this.registry = new RegStorage();
  }

  public void step() {
    if (pc.isHalted()) {
      throw new IllegalStateException("CPU is halted");
    }
    int value = memory.getValueAt(pc.next());
    factory.createInstruction(value).execute(memory, registry, pc, io);
  }

  public void run() {
    if (pc.isHalted()) {
      throw new IllegalStateException("CPU is halted");
    }
    while (!pc.isHalted()) {
      int value = memory.getValueAt(pc.next());
      factory.createInstruction(value).execute(memory, registry, pc, io);
    }
  }

  public void reset() {
    pc.reset();
    registry.reset();
  }

  public void addRegistryListener(StorageListener listener) {
    registry.addListener(listener);
  }

  public Registry getRegistry() {
    return registry;
  }
}
