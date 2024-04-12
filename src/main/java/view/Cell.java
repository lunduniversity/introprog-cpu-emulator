package view;

import java.awt.Container;

public class Cell extends AbstractCell {

  public Cell(
      Container parent, int index, CellValueListener valueListener, CellSelecter cellSelecter) {
    super(parent, index, pad(index), null, valueListener, cellSelecter);
  }
}
