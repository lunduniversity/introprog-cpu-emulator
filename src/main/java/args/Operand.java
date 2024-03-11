package args;

/**
 * Represents an operand in the instruction set. An operand can be a memory address, a register
 * address, or a value. The first bit is used to indicate if it's an address (1) or a value (0). The
 * second bit is used to indicate if it's a memory address (1) or a register address (0).
 *
 * <p>Example:
 *
 * <ul>
 *   <li>0x00 - 0x3F: Value
 *   <li>0x40 - 0x7F: Memory address
 *   <li>0x80 - 0xBF: Register address
 */
public class Operand {
  private int code;

  public static Operand of(int code) {
    return new Operand(code);
  }

  public static Operand mem(int address) {
    // Second bit indicates a memory address, 0100 0000
    return new Operand(address | 0x40);
  }

  public static Operand reg(int address) {
    // First bit indicates a register address, 1000 0000
    return new Operand(address | 0x80);
  }

  public static Operand val(int value) {
    return new Operand(value & 0x3F);
  }

  public static Operand reg(String registerName) {
    switch (registerName) {
      case "OP1":
        return reg(0);
      case "OP2":
        return reg(1);
      case "RES":
        return reg(2);
      case "R1":
        return reg(3);
      case "R2":
        return reg(4);
      case "R3":
        return reg(5);
      default:
        throw new IllegalArgumentException("Invalid register name: " + registerName);
    }
  }

  private Operand(int address) {
    // If any more than 8 bits are set, report an overflow message
    if ((address & 0xFFFFFF00) != 0) {
      // TODO: Report an overflow message
    }
    // Check that the first or second bit is set
    if ((address & 0x80) == 0x80 || (address & 0x40) == 0x40) {
      throw new IllegalArgumentException(
          "Invalid address: " + address + " (must have the first or second bit set)");
    }
    this.code = address & 0xFF; // 8 bits
  }

  public boolean isAddress() {
    // First bit indicates if it's an address
    return (code & 0x80) == 0x80;
  }

  public boolean isValue() {
    return !isAddress();
  }

  public boolean isMemory() {
    // Second bit indicates if it's a memory address (0) or a register address (1)
    return isAddress() && (code & 0x40) == 0x00;
  }

  public boolean isRegister() {
    // Second bit indicates if it's a memory address (0) or a register address (1)
    return isAddress() && (code & 0x40) == 0x40;
  }

  public int getOperand() {
    // Remove the first two bits to get the actual operand
    return code & 0x3F;
  }

  public int getRawOperand() {
    return code;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    Operand other = (Operand) obj;
    return other.code == this.code;
  }

  @Override
  public int hashCode() {
    return code;
  }
}
