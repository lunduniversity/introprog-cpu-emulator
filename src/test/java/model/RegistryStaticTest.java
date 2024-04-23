package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RegistryStaticTest {

  @Test
  void testIdxToName() {
    assertEquals(Registry.REG_OP1, Registry.idxToName(0));
    assertEquals(Registry.REG_OP2, Registry.idxToName(1));
    assertEquals(Registry.REG_RES, Registry.idxToName(2));
    assertEquals(Registry.REG_R1, Registry.idxToName(3));
    assertEquals(Registry.REG_R2, Registry.idxToName(4));
    assertEquals(Registry.REG_R3, Registry.idxToName(5));
    assertEquals(Registry.REG_OUT, Registry.idxToName(6));
  }

  @Test
  void testInvalidIdxToNameReturnsInvalidRegisterConstant() {
    assertEquals(Registry.INVALID_REGISTER, Registry.idxToName(-1));
    assertEquals(Registry.INVALID_REGISTER, Registry.idxToName(8));
  }

  @Test
  void testNameToIdx() {
    assertEquals(0, Registry.nameToIdx(Registry.REG_OP1));
    assertEquals(1, Registry.nameToIdx(Registry.REG_OP2));
    assertEquals(2, Registry.nameToIdx(Registry.REG_RES));
    assertEquals(3, Registry.nameToIdx(Registry.REG_R1));
    assertEquals(4, Registry.nameToIdx(Registry.REG_R2));
    assertEquals(5, Registry.nameToIdx(Registry.REG_R3));
    assertEquals(6, Registry.nameToIdx(Registry.REG_OUT));
  }

  @Test
  void testInvalidNameToIdxThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> Registry.nameToIdx("INVALID"));
  }
}
