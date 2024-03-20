package view;

import model.Memory;

public class CellSelecter {

  private int selectStartRange = -1;
  private int selectEndRange = -1;

  private final int maxRange;

  public CellSelecter(int maxRange) {
    this.maxRange = maxRange;
  }

  private int[] copiedRange = new int[] {};

  public void setSelectedCell(int selectedCell) {
    selectStartRange = selectedCell;
    selectEndRange = selectedCell + 1;
  }

  public void setSelectedRange(int start, int end) {
    selectStartRange = start;
    selectEndRange = end;
  }

  public void addSelectedCell(int selectedCell) {
    if (selectStartRange == -1) {
      selectStartRange = selectedCell;
      selectEndRange = selectedCell + 1;
    } else {
      if (selectedCell < selectStartRange) {
        selectStartRange = selectedCell;
      } else if (selectedCell >= selectEndRange) {
        selectEndRange = selectedCell + 1;
      }
    }
  }

  public void clearSelection() {
    selectStartRange = -1;
    selectEndRange = -1;
  }

  public boolean isSelected() {
    return selectStartRange != -1;
  }

  public int getSelectedCell() {
    return selectStartRange;
  }

  public int[] getSelectedRange() {
    return new int[] {selectStartRange, selectEndRange};
  }

  public void copySelection(Memory memory) {
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

  public void pasteSelection(Memory memory) {
    if (selectStartRange != -1) {
      for (int i = 0; i < copiedRange.length; i++) {
        memory.setValueAt(selectStartRange + i, copiedRange[i]);
      }
    }
  }

  public void forEachSelected(SelecteeAction action) {
    if (selectStartRange != -1) {
      for (int i = selectStartRange; i < selectEndRange; i++) {
        action.execute(i);
      }
    }
  }

  public void moveSelectionUp() {
    if (selectStartRange != -1) {
      selectStartRange = Math.max(0, selectStartRange - 1);
      selectEndRange = Math.max(selectStartRange + 1, selectEndRange - 1);
    }
  }

  public void moveSelectionDown() {
    if (selectStartRange != -1) {
      selectStartRange = Math.min(maxRange - 1, selectStartRange + 1);
      selectEndRange = Math.min(maxRange, selectEndRange + 1);
    }
  }

  public void moveCellsUp(Memory memory) {
    if (selectStartRange != -1) {
      memory.moveCellsUp(selectStartRange, selectEndRange);
      selectStartRange = Math.max(0, selectStartRange - 1);
      selectEndRange = Math.max(selectStartRange + 1, selectEndRange - 1);
    }
  }

  public void moveCellsDown(Memory memory) {
    if (selectStartRange != -1) {
      memory.moveCellsDown(selectStartRange, selectEndRange);
      selectStartRange = Math.min(maxRange - 1, selectStartRange + 1);
      selectEndRange = Math.min(maxRange, selectEndRange + 1);
    }
  }

  public interface SelecteeAction {
    void execute(int address);
  }
}
