package util;

public record IntTuple(int a, int b) {
  public static IntTuple of(int a, int b) {
    return new IntTuple(a, b);
  }
}
