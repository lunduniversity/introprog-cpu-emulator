package view;

import model.Memory;

public class CellSelecter extends AbstractSelecter {

  private final Memory memory;

  public CellSelecter(Memory memory, SelectionPainter painter, FocusRequester focusRequester) {
    super(memory.size(), painter, focusRequester);
    this.memory = memory;
  }

  @Override
  public StorageType _getStorageType() {
    return StorageType.MEMORY;
  }

  // Below are methods that are action rather than selection related, they might be moved to a
  // different class

  public void deleteSelection() {
    if (selectStartRange != -1) {
      // Move all cells after the selection up by the size of the selection
      int size = selectEndRange - selectStartRange;
      for (int i = 0; i < size; i++) {
        int nextValue =
            selectEndRange + i < memory.size() ? memory.getValueAt(selectEndRange + i) : 0;
        memory.setValueAt(selectStartRange + i, nextValue);
      }
      selectEndRange = selectStartRange + 1;
    }
  }

  public boolean isSelected() {
    return selectStartRange != -1;
  }

  public int[] getSelectedRange() {
    return new int[] {selectStartRange, selectEndRange};
  }

  protected void _moveCellsUpHelper() {
    if (selectStartRange != -1) {
      if (memory.moveCellsUp(selectStartRange, selectEndRange)) {
        selectStartRange--;
        selectEndRange--;
        caretPosRow--;
      }
    }
    _paint();
  }

  protected void _moveCellsDownHelper() {
    if (selectStartRange != -1) {
      if (memory.moveCellsDown(selectStartRange, selectEndRange)) {
        selectStartRange++;
        selectEndRange++;
        caretPosRow++;
      }
    }
    _paint();
  }

  @Override
  protected int[] getValuesInRange(int start, int end) {
    int[] values = new int[end - start];
    for (int i = start; i < end; i++) {
      values[i - start] = memory.getValueAt(i);
    }
    return values;
  }

  @Override
  protected void setValuesInRange(int start, int[] values) {
    for (int i = 0; i < values.length; i++) {
      // Check if the index is out of bounds
      if (start + i >= memory.size()) {
        int cutoff = start + values.length - memory.size();
        throw new IndexOutOfBoundsException(
            String.format("Data exceeds memory bounds, %d values were cut off.", cutoff));
      }
      memory.setValueAt(start + i, values[i]);
    }
  }
}
