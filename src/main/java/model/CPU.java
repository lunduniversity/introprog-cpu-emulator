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
    int value = memory.getValueAt(pc.getCurrentIndex());
    factory.createInstruction(value).execute(memory, registry, pc, io);
  }

  public void run() {
    if (pc.isHalted()) {
      throw new IllegalStateException("CPU is halted");
    }
    int prevIdx = -1;
    int totalStepCounter = 0;
    int modCount = -1;
    while (!pc.isHalted()) {
      if (pc.getCurrentIndex() == prevIdx && modCount == io.modCount()) {
        throw new IllegalStateException("Program is stuck. Aborted.");
      }
      if (totalStepCounter > 1000) {
        throw new IllegalStateException(
            "Possible infinite loop detected. Aborted after 1000 steps.");
      }
      int value = memory.getValueAt(pc.getCurrentIndex());
      factory.createInstruction(value).execute(memory, registry, pc, io);
      totalStepCounter++;
      modCount = io.modCount();
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
