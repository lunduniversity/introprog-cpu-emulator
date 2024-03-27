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

  public void copySelection() {
    if (selectStartRange != -1) {
      int size = selectEndRange - selectStartRange;
      copiedRange = new int[size];
      for (int i = 0; i < size; i++) {
        copiedRange[i] = memory.getValueAt(selectStartRange + i);
      }
    }
  }

  public int[] getPasteData() {
    return copiedRange;
  }

  public void pasteSelection() {
    if (selectStartRange != -1) {
      for (int i = 0; i < copiedRange.length; i++) {
        memory.setValueAt(selectStartRange + i, copiedRange[i]);
      }
    }
  }

  public void moveCellsUp() {
    if (selectStartRange != -1) {
      if (memory.moveCellsUp(selectStartRange, selectEndRange)) {
        selectStartRange--;
        selectEndRange--;
        caretPosRow--;
      }
    }
    _paint();
  }

  public void moveCellsDown() {
    if (selectStartRange != -1) {
      if (memory.moveCellsDown(selectStartRange, selectEndRange)) {
        selectStartRange++;
        selectEndRange++;
        caretPosRow++;
      }
    }
    _paint();
  }
}
