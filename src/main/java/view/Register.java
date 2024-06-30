package view;

import java.awt.Container;
import model.Memory;
import model.Registry;

public class Register extends AbstractCell {

  public Register(
      Container parent,
      int index,
      String name,
      CellValueListener valueListener,
      RegisterSelecter registerSelecter,
      Memory mem,
      Registry reg) {
    super(parent, index, pad(index), name, valueListener, registerSelecter, mem, reg, false);
  }
}
