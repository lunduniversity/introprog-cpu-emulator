package model;

import args.Operand;

public interface AddressableStorage {

  public void setValueAt(Operand address, Operand value);

  public int getValueAt(Operand address);
}
