package model;

public interface ProgramCounterListener {
  void onProgramCounterChanged(int oldIdx, int newIdx);

  void onProgramCounterHalted(int haltReson);
}
