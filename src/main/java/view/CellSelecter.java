package view;

import model.Memory;
import util.Range;

public class CellSelecter extends AbstractSelecter {

  private final Memory memory;

  public CellSelecter(Memory memory, SelectionPainter painter, FocusRequester focusRequester) {
    super(memory.size(), painter, focusRequester);
    this.memory = memory;
  }

  @Override
  public StorageType getStorageType() {
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

  protected boolean moveCellsUpDelegater(Range range) {
    return memory.moveCellsUp(range.from(), range.to());
  }

  protected boolean moveCellsDownDelegater(Range range) {
    return memory.moveCellsDown(range.from(), range.to());
  }

  @Override
  protected int[] getRangeDelegater(Range range) {
    return memory.getValuesInRange(range);
  }

  @Override
  protected int setRangeDelegater(Range range, int[] values) {
    return memory.setValuesInRange(range, values);
  }

  @Override
  protected void deleteRange(Range range) {
    memory.deleteCells(range.from(), range.to());
  }
}
