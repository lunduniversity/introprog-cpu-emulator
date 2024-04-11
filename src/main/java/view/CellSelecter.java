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

  protected boolean _moveCellsUpDelegater(int start, int end) {
    return memory.moveCellsUp(start, end);
  }

  protected boolean _moveCellsDownHelper(int start, int end) {
    return memory.moveCellsDown(start, end);
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
  protected int setValuesInRange(int start, int[] values) {
    return memory.setRange(start, values);
  }

  @Override
  protected void _deleteRange(int startIdx, int endIdx) {
    memory.deleteCells(startIdx, endIdx);
  }
}
