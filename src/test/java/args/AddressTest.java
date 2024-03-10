package args;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import model.ObservableStorage;

public class AddressTest {

    @Test
    public void testGetLabel() {
        int addressValue = 0x1A;
        Address address = Address.of(addressValue);
        assertEquals("0x1A", address.getLabel(), "getLabel should return the correct hexadecimal string.");
    }

    @Test
    public void testGetValue() {
        int addressValue = 0x1B;
        int expectedValue = 100;
        Address address = Address.of(addressValue);

        ObservableStorage mockMemory = mock(ObservableStorage.class);
        when(mockMemory.getValueAt(address)).thenReturn(expectedValue);

        assertEquals(expectedValue, address.getValue(mockMemory),
                "getValue should return the correct value from Memory.");
    }
}
