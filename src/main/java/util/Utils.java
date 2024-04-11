package util;

public class Utils {

  public static final Range INVALID_RANGE = new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);

  /** A numeric range [from, to). */
  public record Range(int from, int to) {
    public Range {
      if (from > to) {
        throw new IllegalArgumentException(
            "The 'from' value must be less than or equal to the 'to' value.");
      }
    }

    public Range incTo() {
      return new Range(from, to + 1);
    }

    public Range decTo() {
      return new Range(from, to - 1);
    }

    public Range incFrom() {
      return new Range(from + 1, to);
    }

    public Range decFrom() {
      return new Range(from - 1, to);
    }

    public boolean contains(int value) {
      return from <= value && value < to;
    }

    /**
     * Check if the range's lower bound is above the given value. Like the lower bound itself, the
     * value is inclusive, meaning that the range contains the value. Example: [0, 5) is above 0,
     * but not above 1.
     *
     * @param value The value to compare the range's lower bound to.
     * @return True if the range's lower bound is above the given value, false otherwise.
     */
    public boolean isAbove(int value) {
      return from >= value;
    }

    /**
     * Check if the range's upper bound is below the given value. Like the upper bound itself, the
     * value is exclusive, meaning that the range does not contain the value. Example: [0, 5) is
     * below 5, but not below 4.
     *
     * @param value The value to compare the range's upper bound to.
     * @return True if the range's upper bound is below the given value, false otherwise.
     */
    public boolean isBelow(int value) {
      return to <= value;
    }
  }
}
