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

  protected boolean _moveCellsUpHelper() {
    return registry.moveCellsUp(selectStartRange, selectEndRange);
  }

  protected boolean _moveCellsDownHelper() {
    return registry.moveCellsDown(selectStartRange, selectEndRange);
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
