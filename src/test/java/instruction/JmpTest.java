package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JmpTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
  }

  @Test
  void testJump() {
    int regIdx = Registry.nameToIdx(Registry.REG_R2);
    int destinationAddress = 20; // Example destination address

    when(mockMemory.size()).thenReturn(10);
    when(mockPC.next()).thenReturn(0, 1, 2);
    when(mockRegistry.getValueAt(regIdx)).thenReturn(destinationAddress);

    Jmp jmpInstruction = new Jmp(regIdx);
    jmpInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify that jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }

  @Test
  void testPrettyPrint() {

    String regNames[] =
        new String[] {
          Registry.REG_R1, Registry.REG_R2, Registry.REG_OP1, Registry.REG_RES,
        };
    int regIdxs[] =
        Arrays.stream(regNames).map(Registry::nameToIdx).mapToInt(Integer::intValue).toArray();
    Jmp jmp[] = Arrays.stream(regIdxs).mapToObj(Jmp::new).toArray(Jmp[]::new);

    for (int i = 0; i < jmp.length; i++) {
      String regName = regNames[i];
      String expected = String.format("%s (dst: *%s)", InstructionFactory.INST_NAME_JMP, regName);
      assertEquals(expected, jmp[i].prettyPrint(mockMemory, mockRegistry, 0));
    }
  }
}
