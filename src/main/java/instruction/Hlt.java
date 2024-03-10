package instruction;

import model.AddressableStorage;
import model.ProgramCounter;

public class Hlt extends Instruction {

    public Hlt() {
        super(InstructionFactory.INST_NAME_HLT);
    }

    @Override
    public void execute(AddressableStorage mem, ProgramCounter pc) {
        pc.halt();
    }

}
