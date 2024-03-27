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
        int nextValue =
            selectEndRange + i < maxRange ? registry.getRegister(selectEndRange + i) : 0;
        registry.setRegister(selectStartRange + i, nextValue);
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

  public void copySelection() {
    if (selectStartRange != -1) {
      int size = selectEndRange - selectStartRange;
      copiedRange = new int[size];
      for (int i = 0; i < size; i++) {
        copiedRange[i] = registry.getRegister(selectStartRange + i);
      }
    }
  }

  public int[] getPasteData() {
    return copiedRange;
  }

  public void pasteSelection() {
    if (selectStartRange != -1) {
      for (int i = 0; i < copiedRange.length; i++) {
        registry.setRegister(selectStartRange + i, copiedRange[i]);
      }
    }
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

  public void moveCellsUp() {
    if (selectStartRange != -1) {
      if (registry.moveCellsUp(selectStartRange, selectEndRange)) {
        selectStartRange--;
        selectEndRange--;
        caretPosRow--;
      }
    }
    _paint();
  }

  public void moveCellsDown() {
    if (selectStartRange != -1) {
      if (registry.moveCellsDown(selectStartRange, selectEndRange)) {
        selectStartRange++;
        selectEndRange++;
        caretPosRow++;
      }
    }
    _paint();
  }
}
