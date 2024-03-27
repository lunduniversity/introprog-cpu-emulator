package view;

import static util.LazySwing.checkEDT;
import static util.LazySwing.inv;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSelecter {

  // Whether the component has focus
  protected boolean active = false;

  // The row and column of the caret
  protected int caretPosRow = 0;
  protected int caretPosCol = 0;

  // The start and end of the selection range
  protected int selectStartRange = -1;
  protected int selectEndRange = -1;

  // For mouse selection
  protected int mouseStartIdx;
  protected int mouseEndIdx;
  protected boolean selecting;
  protected boolean paintMouseSelection;

  protected final int maxRange;
  protected final SelectionPainter painter;
  private final FocusRequester focusRequester;

  public AbstractSelecter(int maxRange, SelectionPainter painter, FocusRequester focusRequester) {
    this.maxRange = maxRange;
    this.painter = painter;
    this.focusRequester = focusRequester;
  }

  public enum StorageType {
    MEMORY,
    REGISTER
  }

  public interface FocusRequester {
    void requestFocus(StorageType type);
  }

  protected int[] copiedRange = new int[] {};

  public final void requestFocus() {
    focusRequester.requestFocus(_getStorageType());
  }

  public abstract StorageType _getStorageType();

  public final void setInactive() {
    active = false;
    _paint();
  }

  public final void setActive() {
    active = true;
    _paint();
  }

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

  public abstract void moveCellsUp();

  public abstract void moveCellsDown();

  protected void _paint() {
    if (selecting) {
      inv(
          () -> {
            int start = Math.min(mouseStartIdx, mouseEndIdx);
            int end = Math.max(mouseStartIdx, mouseEndIdx);
            for (int i = 0; i < maxRange; i++) {
              boolean isSelected = i >= start && i <= end && paintMouseSelection;
              painter.paintSelection(i, isSelected, -1, false);
            }
          });
    } else {
      inv(
          () -> {
            for (int i = 0; i < maxRange; i++) {
              boolean isSelected = i >= selectStartRange && i < selectEndRange;
              painter.paintSelection(i, isSelected, i == caretPosRow ? caretPosCol : -1, active);
            }
          });
    }
  }
}
