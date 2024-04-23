package model;

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
    registry.setValueAt(address, value);

    int storedValue = registry.getValueAt(address);
    assertEquals(value, storedValue, "The value retrieved should match the value set.");
  }

  @Test
  void testSetAndGetRegisterNames() {
    registry.setRegister(Registry.REG_OP1, 1);
    registry.setRegister(Registry.REG_OP2, 2);
    registry.setRegister(Registry.REG_RES, 3);
    registry.setRegister(Registry.REG_R1, 4);
    registry.setRegister(Registry.REG_R2, 5);
    registry.setRegister(Registry.REG_R3, 6);
    registry.setRegister(Registry.REG_OUT, 7);

    assertEquals(1, registry.getRegister(Registry.REG_OP1));
    assertEquals(2, registry.getRegister(Registry.REG_OP2));
    assertEquals(3, registry.getRegister(Registry.REG_RES));
    assertEquals(4, registry.getRegister(Registry.REG_R1));
    assertEquals(5, registry.getRegister(Registry.REG_R2));
    assertEquals(6, registry.getRegister(Registry.REG_R3));
    assertEquals(7, registry.getRegister(Registry.REG_OUT));
  }
}
