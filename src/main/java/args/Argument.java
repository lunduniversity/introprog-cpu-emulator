package args;

import model.ObservableStorage;

public interface Argument {

	String getLabel();

	int getValue(ObservableStorage mem);

}
