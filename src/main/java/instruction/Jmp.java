package instruction;

import model.AddressableStorage;
import model.ProgramCounter;

public class Jmp extends Instruction {

    public Jmp() {
        super(InstructionFactory.INST_NAME_JMP);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        pc.jumpTo(pc.next());
    }

}
