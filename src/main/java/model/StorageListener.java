package model;

import args.Operand;

public interface StorageListener {

  void onMemoryChanged(Operand address, int value);
}
