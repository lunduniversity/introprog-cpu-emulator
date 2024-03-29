package view;

public class Register extends AbstractCell {

  public Register(
      int index, String name, CellValueListener valueListener, RegisterSelecter registerSelecter) {
    super(index, pad(index), name, valueListener, registerSelecter);
  }
}
