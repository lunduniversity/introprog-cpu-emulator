package util;

public enum ExecutionSpeed {
  INSTANT(0),
  VERY_FAST(10),
  FAST(150),
  MEDIUM(500),
  SLOW(1000),
  VERY_SLOW(2000);

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
