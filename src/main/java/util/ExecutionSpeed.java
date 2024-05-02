package util;

public enum ExecutionSpeed {
  INSTANT(0),
  FAST(100),
  MEDIUM(300),
  SLOW(500),
  VERY_SLOW(1000);

  private final int delay;

  ExecutionSpeed(int delay) {
    this.delay = delay;
  }

  public int getDelay() {
    return delay;
  }

  @Override
  public String toString() {
    return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
  }
}
