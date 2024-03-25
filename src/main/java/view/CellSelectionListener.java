package view;

public interface CellSelectionListener {
  void onCellSelection(int bitIdx);

  void onStartSelection();

  void onEndSelection();
}
