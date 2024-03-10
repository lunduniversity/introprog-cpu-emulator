package instruction;

import args.Address;
import args.Constant;
import model.AddressableStorage;
import model.ProgramCounter;

public class Add extends Instruction {

    public Add() {
        super(InstructionFactory.INST_NAME_ADD);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        int a = mem.getValueAt(Address.of(pc.next()));
        int b = mem.getValueAt(Address.of(pc.next()));
        int result = (int) (a + b);
        mem.setValueAt(Address.of(pc.next()), Constant.of(result));
    }

}
