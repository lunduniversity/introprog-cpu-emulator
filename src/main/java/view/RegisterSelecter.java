package view;

import model.Registry;
import util.Range;

public class RegisterSelecter extends AbstractSelecter {

  private final Registry registry;

  public RegisterSelecter(
      Registry registry, SelectionPainter painter, FocusRequester focusRequester) {
    super(Registry.NUM_REGISTERS, painter, focusRequester);
    this.registry = registry;
  }

  @Override
  public StorageType getStorageType() {
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

  protected boolean moveCellsUpDelegater(Range range) {
    return registry.moveCellsUp(range.from(), range.to());
  }

  protected boolean moveCellsDownDelegater(Range range) {
    return registry.moveCellsDown(range.from(), range.to());
  }

  @Override
  protected int[] getRangeDelegater(Range range) {
    return registry.getValuesInRange(range);
  }

  @Override
  protected int setRangeDelegater(Range range, int[] values) {
    return registry.setValuesInRange(range, values);
  }

  @Override
  protected void deleteRange(Range range) {
    // Do nothing. Deletion for registers should not be allowed.
  }
}
