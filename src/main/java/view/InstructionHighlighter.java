package view;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Used to keep track of which memory and register cells should be highlighted, to indicate cells
 * that would be affected by the next instruction.
 */
public class InstructionHighlighter {

  private AbstractCell pcFocusCell;
  private AbstractCell[] memoryCells;

  private Consumer<AbstractCell> gainFocusConsumer;
  private Consumer<AbstractCell> loseFocusConsumer;
  private Consumer<AbstractCell> gainHighlightConsumer;
  private Consumer<AbstractCell> loseHighlightConsumer;

  public InstructionHighlighter(
      Consumer<AbstractCell> gainFocusConsumer,
      Consumer<AbstractCell> loseFocusConsumer,
      Consumer<AbstractCell> gainHighlightConsumer,
      Consumer<AbstractCell> loseHighlightConsumer) {
    this.gainFocusConsumer = gainFocusConsumer;
    this.loseFocusConsumer = loseFocusConsumer;
    this.gainHighlightConsumer = gainHighlightConsumer;
    this.loseHighlightConsumer = loseHighlightConsumer;
    this.memoryCells = new Cell[0];
  }

  public void switchCells(AbstractCell newFocus, AbstractCell... newCells) {
    // If the new focus cell is the same as the previous focus cell, do nothing
    if (Objects.equals(newFocus, pcFocusCell) && Arrays.equals(newCells, memoryCells)) {
      return;
    }

    // Clear the previous focus cell and highlights
    if (pcFocusCell != null) {
      loseFocusConsumer.accept(pcFocusCell);
    }
    for (AbstractCell cell : memoryCells) {
      loseHighlightConsumer.accept(cell);
    }

    // Set the new focus cell and highlights
    if (newFocus != null) {
      gainFocusConsumer.accept(newFocus);
    }
    for (AbstractCell cell : newCells) {
      gainHighlightConsumer.accept(cell);
    }

    // Update the state
    memoryCells = newCells;
    pcFocusCell = newFocus;
  }

  public void clearCells() {
    switchCells(null);
  }

  public boolean isAffected(AbstractCell... cellsToCheck) {
    if (cellsToCheck == null || cellsToCheck.length == 0 || memoryCells.length == 0) {
      return false;
    }
    for (AbstractCell c : memoryCells) {
      for (AbstractCell c2 : cellsToCheck) {
        if (c == c2) {
          return true;
        }
      }
    }
    return false;
  }
}
