package view;

import java.awt.Container;
import model.Memory;
import model.Registry;

public class Cell extends AbstractCell {

  public Cell(
      Container parent,
      int index,
      CellValueListener valueListener,
      CellSelecter cellSelecter,
      Memory mem,
      Registry reg) {
    super(parent, index, pad(index), null, valueListener, cellSelecter, mem, reg);
  }
}
