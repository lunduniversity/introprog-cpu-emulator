package model;

import args.Operand;
import instruction.InstructionFactory;

public class Computer {

  private ObservableStorage memory;
  private ObservableStorage registry;
  private ProgramCounter pc;
  private CPU cpu;

  public Computer(int memorySize) {
    this.memory = new ByteStorage(memorySize);
    this.registry = new ByteStorage(6);
    this.pc = new ProgramCounter();
    this.cpu =
        new CPU(
            new AddressableStorage() {
              public void setValueAt(Operand address, Operand value) {
                if (address.isRegister()) {
                  registry.setValueAt(address, value);
                } else if (address.isMemory()) {
                  memory.setValueAt(address, value);
                } else {
                  throw new IllegalArgumentException("Invalid address type");
                }
              }

              public int getValueAt(Operand address) {
                if (address.isRegister()) {
                  return registry.getValueAt(address);
                } else if (address.isMemory()) {
                  return memory.getValueAt(address);
                } else {
                  throw new IllegalArgumentException("Invalid address type");
                }
              }
            },
            pc,
            new InstructionFactory());
  }

  public int memorySize() {
    return memory.size();
  }

  public void addMemoryListener(StorageListener listener) {
    memory.addListener(listener);
  }

  public void addRegistryListener(StorageListener listener) {
    registry.addListener(listener);
  }

  public void addProgramCounterListener(ProgramCounterListener listener) {
    pc.addListener(listener);
  }

  public void setProgramCounter(int value) {
    pc.setCurrentIndex(value);
  }

  public int getProgramCounter() {
    return pc.getCurrentIndex();
  }

  public int readMemory(int address) {
    return memory.getValueAt(Operand.of(address));
  }

  public void writeMemory(int address, int value) {
    memory.setValueAt(Operand.of(address), Operand.of(value));
  }

  public int readRegistry(int address) {
    return registry.getValueAt(Operand.of(address));
  }

  public void writeRegistry(int address, int value) {
    registry.setValueAt(Operand.of(address), Operand.of(value));
  }

  public void step() {
    cpu.step();
  }
}
