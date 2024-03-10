package args;

import model.ObservableStorage;

public class Constant implements Argument {

	private final int value;

	public static Constant of(int value) {
		return new Constant(value);
	}

	private Constant(int value) {
		this.value = value;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public int getValue(ObservableStorage mem) {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		Constant other = (Constant) obj;
		return other.value == this.value;
	}

	@Override
	public int hashCode() {
		return value;
	}

}
