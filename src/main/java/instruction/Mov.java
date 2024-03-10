package instruction;

import args.Address;
import args.Constant;
import model.AddressableStorage;
import model.ProgramCounter;

public class Mov extends Instruction {

    public Mov() {
        super(InstructionFactory.INST_NAME_MOV);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        int a = mem.getValueAt(Address.of(pc.next()));
        mem.setValueAt(Address.of(pc.next()), Constant.of(a));
    }

}
