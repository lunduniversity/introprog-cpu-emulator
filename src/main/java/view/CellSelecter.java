package view;

import static util.LazySwing.checkEDT;
import static util.LazySwing.inv;

import java.util.concurrent.TimeUnit;
import model.Memory;

public class CellSelecter {

  // The row and column of the caret
  private int caretPosRow = -1;
  private int caretPosCol = -1;

  // The start and end of the selection range
  private int selectStartRange = -1;
  private int selectEndRange = -1;

  // For mouse selection
  private int mouseStartIdx;
  private int mouseEndIdx;
  private boolean selecting;
  private boolean paintMouseSelection;

  private final Memory memory;
  private final int maxRange;

  private SelectionPainter painter;

  public CellSelecter(Memory memory, SelectionPainter painter) {
    this.memory = memory;
    this.maxRange = memory.size();
    this.painter = painter;
  }

  private int[] copiedRange = new int[] {};

  public void setCaretPosition(int row, int col) {
    caretPosRow = row;
    caretPosCol = col;
    selectStartRange = selectEndRange = -1;
    _paint();
  }

  public void moveCaretUp() {
    if (caretPosRow > 0) {
      caretPosRow--;
    }
    selectStartRange = selectEndRange = -1;
    _paint();
  }

  public void moveCaretDown() {
    if (caretPosRow < maxRange - 1) {
      caretPosRow++;
    }
    selectStartRange = selectEndRange = -1;
    _paint();
  }

  public void moveCaretLeft() {
    if (caretPosCol > 0) {
      caretPosCol--;
    }
    selectStartRange = selectEndRange = -1;
    _paint();
  }

  public void moveCaretRight() {
    if (caretPosCol < 7) {
      caretPosCol++;
    }
    selectStartRange = selectEndRange = -1;
    _paint();
  }

  public void expandSelectionUp() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange + 1;
    } else {
      selectStartRange = Math.max(0, selectStartRange - 1);
    }
    caretPosRow = selectStartRange;
    caretPosCol = 0;
    _paint();
  }

  public void expandSelectionDown() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange + 1;
    } else {
      selectEndRange = Math.min(maxRange, selectEndRange + 1);
    }
    caretPosRow = selectStartRange;
    caretPosCol = 0;
    _paint();
  }

  public int[] getCaretPosition() {
    return new int[] {caretPosRow, caretPosCol};
  }

  public int getCaretRow() {
    return caretPosRow;
  }

  public int getCaretCol() {
    return caretPosCol;
  }

  public void clearCaret() {
    caretPosRow = -1;
    caretPosCol = -1;
    _paint();
  }

  public void setSelectedCell(int selectedCell) {
    selectStartRange = selectedCell;
    selectEndRange = selectedCell + 1;
    caretPosRow = selectedCell;
    caretPosCol = 0;
    _paint();
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
    caretPosRow = selectedCell;
    caretPosCol = 0;
    _paint();
  }

  public void clearSelection() {
    selectStartRange = -1;
    selectEndRange = -1;
    _paint();
  }

  public void startSelection(int idx) {
    checkEDT();
    mouseStartIdx = idx;
    mouseEndIdx = idx;
    selecting = true;
    paintMouseSelection = false;
    ComputerUI.executor.schedule(
        () -> {
          inv(
              () -> {
                if (selecting && !paintMouseSelection) {
                  paintMouseSelection = true;
                  _paint();
                }
              });
        },
        300,
        TimeUnit.MILLISECONDS);
    _paint();
  }

  public void updateSelection(int idx) {
    if (selecting) {
      checkEDT();
      mouseEndIdx = idx;
      paintMouseSelection = true;
      _paint();
    }
  }

  public void endSelection() {
    checkEDT();
    if (selecting) {
      selectStartRange = Math.min(mouseStartIdx, mouseEndIdx);
      selectEndRange = Math.max(mouseStartIdx, mouseEndIdx) + 1;
      caretPosRow = selectStartRange;
      caretPosCol = 0;
      selecting = false;
    }
    _paint();
  }

  public void cancelSelection() {
    checkEDT();
    selecting = false;
    _paint();
  }

  public boolean isSelecting() {
    checkEDT();
    return selecting;
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

  public void forEachSelected(SelecteeAction action) {
    if (selectStartRange != -1) {
      for (int i = selectStartRange; i < selectEndRange; i++) {
        action.execute(i);
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
      System.out.println("Moving cells up, range: " + selectStartRange + " to " + selectEndRange);
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
      System.out.println("Moving cells down, range: " + selectStartRange + " to " + selectEndRange);
      if (memory.moveCellsDown(selectStartRange, selectEndRange)) {
        selectStartRange++;
        selectEndRange++;
        caretPosRow++;
      }
    }
    _paint();
  }

  private void _paint() {
    if (selecting) {
      inv(
          () -> {
            int start = Math.min(mouseStartIdx, mouseEndIdx);
            int end = Math.max(mouseStartIdx, mouseEndIdx);
            for (int i = 0; i < maxRange; i++) {
              boolean isSelected = i >= start && i <= end && paintMouseSelection;
              painter.paintSelection(i, isSelected, -1);
            }
          });
    } else {
      inv(
          () -> {
            for (int i = 0; i < maxRange; i++) {
              boolean isSelected = i >= selectStartRange && i < selectEndRange;
              painter.paintSelection(i, isSelected, i == caretPosRow ? caretPosCol : -1);
            }
          });
    }
  }

  public interface SelecteeAction {
    void execute(int address);
  }
}
