package model;

import util.Range;

public interface ObservableStorage {
  void addListener(StorageListener listener);

  /**
   * Set a value in the storage.
   *
   * @param address The index of the value to set.
   * @param value The value to set in the storage.
   */
  void setValueAt(int address, int value);

  /**
   * Set a range of values in the storage.
   *
   * @param range The starting index of the range, inclusive.
   * @param values An array of values to set in the storage.
   * @return The number of values that could not be set, due to the range being out of bounds.
   */
  int setValuesInRange(Range range, int[] values);

  /**
   * Get a value from the storage.
   *
   * @param address The index of the value to get.
   * @return The value at the given index.
   */
  int getValueAt(int address);

  /**
   * Get a range of values from the storage.
   *
   * @param range The starting index of the range, inclusive.
   * @return An array of values from the storage.
   */
  int[] getValuesInRange(Range range);

  /**
   * Move a range of cells up by one cell.
   *
   * @param startIdx The starting index of the range, inclusive.
   * @param endIdx The ending index of the range, exclusive.
   * @return True if the move was successful, false otherwise.
   */
  boolean moveCellsUp(int startIdx, int endIdx);

  /**
   * Move a range of cells down by one cell.
   *
   * @param startIdx The starting index of the range, inclusive.
   * @param endIdx The ending index of the range, exclusive.
   * @return True if the move was successful, false otherwise.
   */
  boolean moveCellsDown(int startIdx, int endIdx);

  /**
   * Delete a range of cells.
   *
   * @param startIdx The starting index of the range, inclusive.
   * @param endIdx The ending index of the range, exclusive.
   */
  void deleteCells(int startIdx, int endIdx);

  /**
   * Get the size of the storage.
   *
   * @return The size of the storage.
   */
  void reset();
}
