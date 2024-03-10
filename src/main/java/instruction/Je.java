package instruction;

import args.Address;
import model.AddressableStorage;
import model.ProgramCounter;

public class Je extends Instruction {

    public Je() {
        super(InstructionFactory.INST_NAME__JE);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        int a = mem.getValueAt(Address.of(pc.next()));
        int b = mem.getValueAt(Address.of(pc.next()));
        if (a == b) {
            pc.jumpTo(pc.next());
        } else {
            pc.next();
        }
    }
}
