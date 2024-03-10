package instruction;

import args.Address;
import args.Constant;
import model.AddressableStorage;
import model.ProgramCounter;

public class Sub extends Instruction {

    public Sub() {
        super(InstructionFactory.INST_NAME_SUB);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        int a = mem.getValueAt(Address.of(pc.next()));
        int b = mem.getValueAt(Address.of(pc.next()));
        int result = (int) (a - b);
        mem.setValueAt(Address.of(pc.next()), Constant.of(result));
    }

}
