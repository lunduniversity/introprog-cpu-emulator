package instruction;

import args.Address;
import args.Constant;
import model.AddressableStorage;
import model.ProgramCounter;

public class St extends Instruction {

    public St() {
        super(InstructionFactory.INST_NAME__ST);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        int a = mem.getValueAt(Address.of(pc.next()));
        mem.setValueAt(Address.of(pc.next()), Constant.of(a));
    }

}
