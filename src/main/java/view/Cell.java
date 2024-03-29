package view;

public class Cell extends AbstractCell {

  public Cell(int index, CellValueListener valueListener, CellSelecter cellSelecter) {
    super(index, pad(index), null, valueListener, cellSelecter);
  }
}
