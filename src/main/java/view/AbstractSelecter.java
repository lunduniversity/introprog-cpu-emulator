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
import util.Range;

public abstract class AbstractSelecter {

  private static final String PASTE_ERROR = "Paste Error";

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
    focusRequester.requestFocus(getStorageType());
  }

  public abstract StorageType getStorageType();

  public final void setInactive() {
    active = false;
    paint();
  }

  public final void setActive() {
    active = true;
    paint();
  }

  // Empty so that subclasses can override
  public void caretMoved() {}

  public void setCaretPosition(int row, int col) {
    caretPosRow = row;
    caretPosCol = col;
    clearSelection();
    paint();
    caretMoved();
  }

  public void moveCaretUp() {
    if (caretPosRow > 0) {
      caretPosRow--;
    }
    clearSelection();
    paintRange(new Range(caretPosRow, caretPosRow + 2));
    caretMoved();
  }

  public void moveCaretDown() {
    if (caretPosRow < maxRange - 1) {
      caretPosRow++;
    }
    clearSelection();
    paintRange(new Range(caretPosRow - 1, caretPosRow + 1));
    caretMoved();
  }

  public void moveCaretLeft() {
    if (caretPosCol > 0) {
      caretPosCol--;
    }
    clearSelection();
    paintRange(new Range(caretPosRow, caretPosRow + 1));
    caretMoved();
  }

  public void moveCaretRight() {
    if (caretPosCol < 7) {
      caretPosCol++;
    }
    clearSelection();
    paintRange(new Range(caretPosRow, caretPosRow + 1));
    caretMoved();
  }

  public void moveCaretToNextCell() {
    if (caretPosRow < maxRange - 1) {
      caretPosRow++;
      caretPosCol = 0;
    }
    clearSelection();
    paintRange(new Range(caretPosRow - 1, caretPosRow + 1));
    caretMoved();
  }

  public void expandSelectionUp() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange;
      paintRange(new Range(selectEndRange, selectStartRange + 1));
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
        paintRange(new Range(selectStartRange, oldEnd));
      } else {
        paintRange(new Range(selectEndRange, selectStartRange + 1));
      }
    }
  }

  public void expandSelectionDown() {
    if (selectStartRange == -1) {
      selectStartRange = caretPosRow;
      selectEndRange = selectStartRange + 1;
      paintRange(new Range(selectStartRange, selectEndRange));
    } else {
      int oldEnd = selectEndRange;
      if (selectStartRange >= selectEndRange) {
        selectEndRange = Math.min(maxRange, selectEndRange + 1);
        if (selectStartRange == selectEndRange) {
          selectEndRange = Math.min(maxRange, selectEndRange + 1);
        }
        paintRange(new Range(oldEnd, selectEndRange));
      } else {
        selectEndRange = Math.min(maxRange, selectEndRange + 1);
        paintRange(new Range(selectStartRange, selectEndRange));
      }
    }
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
    Range oldPos = new Range(caretPosRow, caretPosRow + 1);
    caretPosRow = -1;
    caretPosCol = -1;
    paintRange(oldPos);
  }

  public void setSelectedCell(int selectedCell) {
    selectStartRange = selectedCell;
    selectEndRange = selectedCell + 1;
    caretPosRow = selectedCell;
    caretPosCol = 0;
    paint();
    caretMoved();
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
    paint();
  }

  protected void clearSelection() {
    if (selectStartRange != -1) {
      Range selection = getProperSelectionRange();
      selectStartRange = selectEndRange = -1;
      paintRange(selection);
    }
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
                    paint();
                  }
                }),
        300,
        TimeUnit.MILLISECONDS);
    paint();
  }

  public void updateSelection(int idx) {
    if (mouseSelectingOngoing) {
      Range oldRange = getProperRange(mouseStartIdx, mouseEndIdx, true);
      Range newRange = getProperRange(mouseStartIdx, idx, true);
      mouseEndIdx = idx;
      paintMouseSelection = true;
      paintRange(newRange.merge(oldRange));
    }
  }

  public void endSelection() {
    if (mouseSelectingOngoing) {
      Range range = getProperRange(mouseStartIdx, mouseEndIdx, true);
      selectStartRange = range.from();
      selectEndRange = range.to();
      mouseSelectingOngoing = false;
      paintRange(range);
    }
  }

  public void cancelSelection() {
    mouseSelectingOngoing = false;
    paint();
  }

  public boolean isMouseSelectingOngoing() {
    return mouseSelectingOngoing;
  }

  public void moveSelectionUp() {
    Range selection = getProperSelectionRange();
    if (selection.isAbove(1)) {
      selectStartRange--;
      selectEndRange--;
      caretPosRow--;
      paintRange(selection.decFrom());
    }
  }

  public void moveSelectionDown() {
    Range selection = getProperSelectionRange();
    if (selection.isBelow(maxRange - 1)) {
      selectStartRange++;
      selectEndRange++;
      caretPosRow++;
      paintRange(selection.incTo());
    }
  }

  public final void moveCellsUp() {
    if (selectStartRange == -1) {
      expandSelectionUp();
    }

    Range range = getProperSelectionRange();

    if (moveCellsUpDelegater(range)) {
      selectStartRange--;
      selectEndRange--;
      caretPosRow--;
      paintRange(range.decFrom());
    }
  }

  public final void moveCellsDown() {
    if (selectStartRange == -1) {
      expandSelectionDown();
    }

    Range range = getProperSelectionRange();

    if (moveCellsDownDelegater(range)) {
      selectStartRange++;
      selectEndRange++;
      caretPosRow++;
      paintRange(range.incTo());
    }
  }

  protected abstract boolean moveCellsUpDelegater(Range range);

  protected abstract boolean moveCellsDownDelegater(Range range);

  protected abstract int[] getRangeDelegater(Range range);

  protected abstract int setRangeDelegater(Range range, int[] values);

  public final void copySelection() {
    Range range = getProperSelectionRange();
    if (!range.isValid()) {
      expandSelectionUp(); // Up or down doesn't matter, just select current row
    }
    int[] selectedValues = getRangeDelegater(range);
    StringBuilder sb = new StringBuilder();
    for (int v : selectedValues) {
      sb.append(Instruction.toBinaryString(v, 8, 4)).append("\n");
    }
    StringSelection selection = new StringSelection(sb.toString());
    clipboard.setContents(selection, null);
  }

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
        int excess = setRangeDelegater(new Range(caretPosRow, caretPosRow + values.length), values);
        if (excess > 0) {
          JOptionPane.showMessageDialog(
              null,
              String.format(
                  "Pasted data exceeds the available space. The last %d values were cut off.",
                  excess),
              PASTE_ERROR,
              JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (UnsupportedFlavorException e) {
        JOptionPane.showMessageDialog(
            null,
            "Can only paste binary data, e.g.:\n0101 1010\n1010 0101\n...",
            PASTE_ERROR,
            JOptionPane.WARNING_MESSAGE);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
            null, "An error occurred while pasting data.", PASTE_ERROR, JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  protected void drawCaret() {
    if (caretPosRow != -1) {
      paintRange(new Range(caretPosRow, caretPosRow + 1));
    }
  }

  protected void paint() {
    paintRange(new Range(0, maxRange));
  }

  protected void paintRange(Range range) {
    int safeMin = Math.max(0, range.from());
    int safeMax = Math.min(range.to(), maxRange);
    if (mouseSelectingOngoing) {
      inv(() -> paintMouseSelection(safeMin, safeMax));
    } else if (selectStartRange != -1) {
      inv(() -> paintRangeSelection(safeMin, safeMax));
    } else {
      inv(() -> paintDefaultSelection(safeMin, safeMax));
    }
  }

  private void paintMouseSelection(int safeMin, int safeMax) {
    Range mouseRange = getProperRange(mouseStartIdx, mouseEndIdx, true);
    for (int i = safeMin; i < safeMax; i++) {
      boolean isSelected = mouseRange.contains(i) && paintMouseSelection;
      painter.paintSelection(i, isSelected, -1, false);
    }
  }

  private void paintRangeSelection(int safeMin, int safeMax) {
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

  private void paintDefaultSelection(int safeMin, int safeMax) {
    for (int i = safeMin; i < safeMax; i++) {
      boolean isCaretPosition = i == caretPosRow;
      int selectionColumn = isCaretPosition ? caretPosCol : -1;
      painter.paintSelection(i, false, selectionColumn, active);
    }
  }

  public void clearSelectedCells() {
    Range selection = getProperSelectionRange();
    if (selection.isValid()) {
      int[] zeros = new int[selection.length()];
      setRangeDelegater(getProperSelectionRange(), zeros);
    } else {
      setRangeDelegater(new Range(caretPosRow), new int[] {0});
    }
  }

  public void deleteSelectedCells() {
    Range selection = getProperSelectionRange();
    if (selection.isValid()) {
      deleteRange(selection);
    } else {
      deleteRange(new Range(caretPosRow));
    }
    clearSelection();
    paint();
  }

  protected abstract void deleteRange(Range range);

  private Range getProperRange(int start, int end) {
    return getProperRange(start, end, false);
  }

  private Range getProperRange(int start, int end, boolean inclusive) {
    if (start < end) {
      return new Range(start, end + (inclusive ? 1 : 0));
    } else {
      return new Range(end, start + 1);
    }
  }

  private Range getProperSelectionRange() {
    if (selectStartRange == -1) {
      return Range.invalidRange();
    } else {
      return getProperRange(selectStartRange, selectEndRange);
    }
  }
}
