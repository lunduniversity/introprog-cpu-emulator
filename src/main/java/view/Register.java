package view;

import java.awt.Container;

public class Register extends AbstractCell {

  public Register(
      Container parent,
      int index,
      String name,
      CellValueListener valueListener,
      RegisterSelecter registerSelecter) {
    super(parent, index, pad(index), name, valueListener, registerSelecter);
  }
}
