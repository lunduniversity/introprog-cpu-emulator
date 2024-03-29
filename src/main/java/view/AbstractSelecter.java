package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
      mouseEndIdx = idx;
      paintMouseSelection = true;
      _paint();
    }
  }

  public void endSelection() {
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
    selecting = false;
    _paint();
  }

  public boolean isSelecting() {
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

  public final void moveCellsUp() {
    if (selectStartRange == -1) {
      expandSelectionUp();
    }
    _moveCellsUpHelper();
  }

  public final void moveCellsDown() {
    if (selectStartRange == -1) {
      expandSelectionDown();
    }
    _moveCellsDownHelper();
  }

  protected abstract void _moveCellsUpHelper();

  protected abstract void _moveCellsDownHelper();

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
        String[] lines = data.replaceAll(" ", "").split("\n");
        int[] values = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
          values[i] = Integer.parseInt(lines[i].trim(), 2);
        }
        setValuesInRange(caretPosRow, values);
      } catch (IndexOutOfBoundsException e) {
        JOptionPane.showMessageDialog(
            null, e.getMessage(), "Paste Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(
            null,
            "Can only paste binary data, e.g.:\n0101 1010\n1010 0101\n...",
            "Paste Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  protected abstract void setValuesInRange(int start, int[] values);

  protected void _paint() {
    Runnable r =
        selecting
            ? () -> { // If mouse selection is active
              int start = Math.min(mouseStartIdx, mouseEndIdx);
              int end = Math.max(mouseStartIdx, mouseEndIdx);
              for (int i = 0; i < maxRange; i++) {
                boolean isSelected = i >= start && i <= end && paintMouseSelection;
                painter.paintSelection(i, isSelected, -1, false);
              }
            }
            : () -> {
              for (int i = 0; i < maxRange; i++) {
                boolean isSelected = i >= selectStartRange && i < selectEndRange;
                painter.paintSelection(i, isSelected, i == caretPosRow ? caretPosCol : -1, active);
              }
            };
    inv(r, false);
  }
}
