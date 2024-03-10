package model;

import args.Address;
import args.Argument;

public interface AddressableStorage {

    public void setValueAt(Address address, Argument value);

    public int getValueAt(Address address);

    public int size();

}
