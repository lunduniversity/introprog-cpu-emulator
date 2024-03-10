package model;

import args.Address;
import args.Argument;
import args.Constant;

public class Computer {

    private ObservableStorage memory;
    private ObservableStorage registry;
    private ProgramCounter pc;
    private CPU cpu;

    public Computer(int memorySize, int registrySize) {
        this.memory = new ByteStorage(memorySize);
        this.registry = new ByteStorage(registrySize);
        this.pc = new ProgramCounter();
        this.cpu = new CPU(new AddressableStorage() {
            public void setValueAt(Address address, Argument value) {
                if (address.isRegister()) {
                    registry.setValueAt(address, value);
                } else {
                    memory.setValueAt(address, value);
                }
            }

            public int getValueAt(Address address) {
                if (address.isRegister()) {
                    return registry.getValueAt(address);
                } else {
                    return memory.getValueAt(address);
                }
            }

            public int size() {
                return memory.size();
            }

        }, pc);
    }

    public int memorySize() {
        return memory.size();
    }

    public int registrySize() {
        return registry.size();
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
        return memory.getValueAt(Address.of(address));
    }

    public void writeMemory(int address, int value) {
        memory.setValueAt(Address.of(address), Constant.of(value));
    }

    public int readRegistry(int address) {
        return registry.getValueAt(Address.of(address));
    }

    public void writeRegistry(int address, int value) {
        registry.setValueAt(Address.of(address), Constant.of(value));
    }

    public void step() {
        cpu.step();
    }

    public void loadProgram(int[] program) {
        for (int i = 0; i < program.length; i++) {
            memory.setValueAt(Address.of(i), Constant.of(program[i]));
        }
    }

}
