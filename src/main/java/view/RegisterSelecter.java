package view;

import model.Registry;

public class RegisterSelecter extends AbstractSelecter {

  private final Registry registry;

  public RegisterSelecter(
      Registry registry, SelectionPainter painter, FocusRequester focusRequester) {
    super(Registry.NUM_REGISTERS, painter, focusRequester);
    this.registry = registry;
  }

  @Override
  public StorageType _getStorageType() {
    return StorageType.REGISTER;
  }

  public void deleteSelection() {
    if (selectStartRange != -1) {
      // Move all cells after the selection up by the size of the selection
      int size = selectEndRange - selectStartRange;
      for (int i = 0; i < size; i++) {
        int nextValue = selectEndRange + i < maxRange ? registry.getValueAt(selectEndRange + i) : 0;
        registry.setValueAt(selectStartRange + i, nextValue);
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

  public int[] getPasteData() {
    return copiedRange;
  }

  public void moveSelectionUp() {
    if (selectStartRange > 0) {
      selectStartRange--;
      selectEndRange--;
      caretPosRow--;
      _paint();
    }
  }

  public void moveSelectionDown() {
    if (selectStartRange != -1 && selectEndRange < maxRange) {
      selectStartRange++;
      selectEndRange++;
      caretPosRow++;
      _paint();
    }
  }

  protected void _moveCellsUpHelper() {
    if (selectStartRange != -1) {
      if (registry.moveCellsUp(selectStartRange, selectEndRange)) {
        selectStartRange--;
        selectEndRange--;
        caretPosRow--;
      }
    }
    _paint();
  }

  protected void _moveCellsDownHelper() {
    if (selectStartRange != -1) {
      if (registry.moveCellsDown(selectStartRange, selectEndRange)) {
        selectStartRange++;
        selectEndRange++;
        caretPosRow++;
      }
    }
    _paint();
  }

  @Override
  protected int[] getValuesInRange(int start, int end) {
    registry.getRange(start, end);
    int[] values = new int[end - start];
    for (int i = start; i < end; i++) {
      values[i - start] = registry.getValueAt(i);
    }
    return values;
  }

  @Override
  protected int setValuesInRange(int start, int[] values) {
    return registry.setRange(start, values);
  }

  @Override
  protected void _deleteRange(int startIdx, int endIdx) {
    // Do nothing. Deletion for registers should not be allowed.
  }
}
