package model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegStorageTest {

  private Registry registry;

  @BeforeEach
  void setUp() {
    registry = new RegStorage();
  }

  @Test
  void testSetAndGetRegister() {
    int address = 5;
    int value = 12;
    registry.setRegister(address, value);

    int storedValue = registry.getRegister(address);
    assertEquals(value, storedValue, "The value retrieved should match the value set.");
  }

  @Test
  void testSetAndGetRegisterNames() {
    registry.setRegister("OP1", 1);
    registry.setRegister("OP2", 2);
    registry.setRegister("RES", 3);
    registry.setRegister("R1", 4);
    registry.setRegister("R2", 5);
    registry.setRegister("R3", 6);
    registry.setRegister("PRT", 7);

    assertEquals(1, registry.getRegister("OP1"));
    assertEquals(2, registry.getRegister("OP2"));
    assertEquals(3, registry.getRegister("RES"));
    assertEquals(4, registry.getRegister("R1"));
    assertEquals(5, registry.getRegister("R2"));
    assertEquals(6, registry.getRegister("R3"));
    assertEquals(7, registry.getRegister("PRT"));
  }

  @Test
  void testGetNumRegisters() {
    assertEquals(7, registry.getNumRegisters());
  }

  @Test
  void testGetRegisterNames() {
    String[] expected = {"OP1", "OP2", "RES", "R1", "R2", "R3", "PRT"};
    assertArrayEquals(expected, registry.getRegisterNames());
  }
}
