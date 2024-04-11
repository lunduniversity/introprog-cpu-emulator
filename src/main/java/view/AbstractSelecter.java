package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public abstract class AbstractSelecter {

  private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

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
  protected boolean mouseSelectingOngoing;
  protected boolean paintMouseSelection;

  protected final int maxRange;
  protected final SelectionPainter painter;
  private final FocusRequester focusRequester;

  protected AbstractSelecter(
      int maxRange, SelectionPainter painter, FocusRequester focusRequester) {
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
    resetSelection();
    _paint();
  }

  public void moveCaretUp() {
    if (caretPosRow > 0) {
      caretPosRow--;
    }
    resetSelection();
    resetSelection();
    _paintRange(caretPosRow, caretPosRow + 2);
  }

  public void moveCaretDown() {
    if (caretPosRow < maxRange - 1) {
      caretPosRow++;
    }
    resetSelection();
    _paintRange(caretPosRow - 1, caretPosRow + 1);
  }

  public void moveCaretLeft() {
    if (caretPosCol > 0) {
      caretPosCol--;
    }
    resetSelection();
    _paintRange(caretPosRow, caretPosRow + 1);
  }

  public void moveCaretRight() {
    if (caretPosCol < 7) {
      caretPosCol++;
    }
    resetSelection();
    _paintRange(caretPosRow, caretPosRow + 1);
  }

  public void moveCaretToNextCell() {
    if (caretPosRow < maxRange - 1) {
      caretPosRow++;
      caretPosCol = 0;
    }
    resetSelection();
    _paintRange(caretPosRow - 1, caretPosRow + 1);
  }

  public void expandSelectionUp() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange;
      _paintRange(selectEndRange, selectStartRange + 1);
    } else {
      int oldEnd = selectEndRange;
      if (selectStartRange == selectEndRange - 1) {
        if (selectStartRange > 0) {
          selectEndRange = selectStartRange - 1;
        } // else, do nothing, selection is already at the top
      } else {
        selectEndRange = Math.max(0, selectEndRange - 1);
      }
      if (selectStartRange <= selectEndRange) {
        _paintRange(selectStartRange, oldEnd);
      } else {
        _paintRange(selectEndRange, selectStartRange + 1);
      }
    }
    _paint();
  }

  public void expandSelectionDown() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange + 1;
      _paintRange(selectStartRange, selectEndRange);
    } else {
      int oldEnd = selectEndRange;
      if (selectStartRange >= selectEndRange) {
        selectEndRange = Math.min(maxRange, selectEndRange + 1);
        if (selectStartRange == selectEndRange) {
          selectEndRange = Math.min(maxRange, selectEndRange + 1);
        }
        _paintRange(oldEnd, selectEndRange);
      } else {
        selectEndRange = Math.min(maxRange, selectEndRange + 1);
        _paintRange(selectStartRange, selectEndRange);
      }
    }
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
    int oldCaretRow = caretPosRow;
    caretPosRow = -1;
    caretPosCol = -1;
    _paintRange(oldCaretRow, oldCaretRow + 1);
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
    _paint();
  }

  public void clearSelection() {
    selectStartRange = -1;
    selectEndRange = -1;
    _paint();
  }

  public void startSelection(int idx) {
    mouseStartIdx = idx;
    mouseEndIdx = idx;
    mouseSelectingOngoing = true;
    paintMouseSelection = false;
    ComputerUI.executor.schedule(
        () ->
            inv(
                () -> {
                  if (mouseSelectingOngoing && !paintMouseSelection) {
                    paintMouseSelection = true;
                    _paint();
                  }
                }),
        300,
        TimeUnit.MILLISECONDS);
    _paint();
  }

  public void updateSelection(int idx) {
    if (mouseSelectingOngoing) {
      mouseEndIdx = idx;
      paintMouseSelection = true;
      _paint();
    }
  }

  public void endSelection() {
    if (mouseSelectingOngoing) {
      selectStartRange = Math.min(mouseStartIdx, mouseEndIdx);
      selectEndRange = Math.max(mouseStartIdx, mouseEndIdx) + 1;
      mouseSelectingOngoing = false;
    }
    _paint();
  }

  public void cancelSelection() {
    mouseSelectingOngoing = false;
    _paint();
  }

  public boolean isMouseSelectingOngoing() {
    return mouseSelectingOngoing;
  }

  public void moveSelectionUp() {
    if (selectStartRange > 0 && selectEndRange > 0) {
      int oldStart = selectStartRange;
      int oldEnd = selectEndRange;
      selectStartRange--;
      selectEndRange--;
      caretPosRow--;
      _paintRange(_min(selectStartRange, selectEndRange), _max(oldStart + 1, oldEnd));
    }
  }

  public void moveSelectionDown() {
    if (selectStartRange != -1 && selectStartRange < maxRange && selectEndRange < maxRange) {
      int oldStart = selectStartRange;
      int oldEnd = selectEndRange;
      selectStartRange++;
      selectEndRange++;
      caretPosRow++;
      _paintRange(_min(oldStart, oldEnd), _max(selectStartRange + 1, selectEndRange));
    }
  }

  public final void moveCellsUp() {
    if (selectStartRange == -1) {
      expandSelectionUp();
    } else if (_moveCellsUpHelper()) {
      int oldStart = selectStartRange;
      int oldEnd = selectEndRange;
      selectStartRange--;
      selectEndRange--;
      caretPosRow--;
      _paintRange(
          _min(oldStart, selectStartRange, oldEnd, selectEndRange),
          _max(oldStart, selectStartRange, oldEnd, selectEndRange));
    }
  }

  public final void moveCellsDown() {
    if (selectStartRange == -1) {
      expandSelectionDown();
    } else if (_moveCellsDownHelper()) {
      int oldStart = selectStartRange;
      int oldEnd = selectEndRange;
      selectStartRange++;
      selectEndRange++;
      caretPosRow++;
      _paintRange(
          _min(oldStart, selectStartRange, oldEnd, selectEndRange),
          _max(oldStart, selectStartRange, oldEnd, selectEndRange));
    }
  }

  protected abstract boolean _moveCellsUpHelper();

  protected abstract boolean _moveCellsDownHelper();

  public final void copySelection() {
    if (selectStartRange == -1) {
      expandSelectionUp(); // Up or down doesn't matter, just select current row
    }
    int[] selectedValues = getValuesInRange(selectStartRange, selectEndRange);
    StringBuilder sb = new StringBuilder();
    for (int v : selectedValues) {
      sb.append(Instruction.toBinaryString(v, 8, 4)).append("\n");
    }
    StringSelection selection = new StringSelection(sb.toString());
    clipboard.setContents(selection, null);
  }

  protected abstract int[] getValuesInRange(int start, int end);

  public final void pasteSelection() {
    Transferable contents = clipboard.getContents(null);
    if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        String data = (String) contents.getTransferData(DataFlavor.stringFlavor);
        String[] lines = data.replace(" ", "").split("\n");
        int[] values = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
          values[i] = Integer.parseInt(lines[i].trim(), 2);
        }
        int excess = setValuesInRange(caretPosRow, values);
        if (excess > 0) {
          JOptionPane.showMessageDialog(
              null,
              String.format("Pasted data exceeds the available space. Cut off %d values.", excess),
              "Paste Error",
              JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (UnsupportedFlavorException e) {
        JOptionPane.showMessageDialog(
            null,
            "Can only paste binary data, e.g.:\n0101 1010\n1010 0101\n...",
            "Paste Error",
            JOptionPane.WARNING_MESSAGE);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
            null,
            "An error occurred while pasting data.",
            "Paste Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  protected abstract int setValuesInRange(int start, int[] values);

  protected void resetSelection() {
    if (selectStartRange != -1) {
      int start = Math.min(selectStartRange, selectEndRange);
      int end = Math.max(selectStartRange, selectEndRange);
      selectStartRange = selectEndRange = -1;
      _paintRange(start, end + 1);
    }
  }

  protected void drawCaret() {
    if (caretPosRow != -1) {
      _paintRange(caretPosRow, caretPosRow + 1);
    }
  }

  protected void _paint() {
    _paintRange(0, maxRange);
  }

  protected void _paintRange(int range[]) {
    _paintRange(range[0], range[1]);
  }

  protected void _paintRange(int min, int max) {
    System.out.println(
        "Painting range: "
            + min
            + " to "
            + max
            + " (actual range: "
            + selectStartRange
            + " to "
            + selectEndRange
            + ")");
    int safeMin = Math.max(0, min);
    int safeMax = Math.min(max, maxRange);
    Runnable r =
        mouseSelectingOngoing
            ? () -> { // If mouse selection is active
              int start = Math.min(mouseStartIdx, mouseEndIdx);
              int end = Math.max(mouseStartIdx, mouseEndIdx);
              for (int i = safeMin; i < safeMax; i++) {
                boolean isSelected = start <= i && i <= end && paintMouseSelection;
                painter.paintSelection(i, isSelected, -1, false);
              }
            }
            : selectStartRange != -1
                ? () -> {
                  int start = Math.min(selectStartRange, selectEndRange);
                  int end = Math.max(selectStartRange, selectEndRange);
                  if (selectStartRange >= selectEndRange) {
                    end++;
                  }
                  for (int i = safeMin; i < safeMax; i++) {
                    boolean isSelected = start <= i && i < end;
                    painter.paintSelection(i, isSelected, -1, active);
                  }
                }
                : () -> {
                  for (int i = safeMin; i < safeMax; i++) {
                    painter.paintSelection(i, false, i == caretPosRow ? caretPosCol : -1, active);
                  }
                };
    inv(r);
  }

  public void clearSelectedCells() {
    if (selectStartRange != -1) {
      int[] zeros = new int[selectEndRange - selectStartRange];
      setValuesInRange(selectStartRange, zeros);
    } else {
      setValuesInRange(caretPosRow, new int[] {0});
    }
  }

  public void deleteSelectedCells() {
    if (selectStartRange != -1) {
      _deleteRange(selectStartRange, selectEndRange);
    } else {
      _deleteRange(caretPosRow, 1);
    }
    resetSelection();
    _paint();
  }

  protected abstract void _deleteRange(int startIdx, int endIdx);

  private int _min(int... values) {
    int min = Integer.MAX_VALUE;
    for (int v : values) {
      min = Math.min(min, v);
    }
    return min;
  }

  private int _max(int... values) {
    int max = Integer.MIN_VALUE;
    for (int v : values) {
      max = Math.max(max, v);
    }
    return max;
  }

  private int[] range(int delta, int... values) {
    int min = _min(values);
    int max = _max(values);
    if (delta < 0) {
      return new int[] {min + delta, max};
    }
    if (delta > 0) {
      return new int[] {min, max + delta};
    }
    return new int[] {min, max};
  }
}
