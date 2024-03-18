package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RegistryStaticTest {

  @Test
  void testIdxToName() {
    assertEquals("OP1", Registry.idxToName(0));
    assertEquals("OP2", Registry.idxToName(1));
    assertEquals("RES", Registry.idxToName(2));
    assertEquals("R1", Registry.idxToName(3));
    assertEquals("R2", Registry.idxToName(4));
    assertEquals("R3", Registry.idxToName(5));
    assertEquals("PRT", Registry.idxToName(6));
  }

  @Test
  void testInvalidIdxToNameReturnsInvalidRegisterConstant() {
    assertEquals(Registry.INVALID_REGISTER, Registry.idxToName(-1));
    assertEquals(Registry.INVALID_REGISTER, Registry.idxToName(7));
  }

  @Test
  void testNameToIdx() {
    assertEquals(0, Registry.nameToIdx("OP1"));
    assertEquals(1, Registry.nameToIdx("OP2"));
    assertEquals(2, Registry.nameToIdx("RES"));
    assertEquals(3, Registry.nameToIdx("R1"));
    assertEquals(4, Registry.nameToIdx("R2"));
    assertEquals(5, Registry.nameToIdx("R3"));
    assertEquals(6, Registry.nameToIdx("PRT"));
  }

  @Test
  void testInvalidNameToIdxThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> Registry.nameToIdx("INVALID"));
  }
}
