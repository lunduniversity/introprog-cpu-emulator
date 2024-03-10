package args;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ConstantTest {

    @Test
    public void testGetValue() {
        int value = 120;
        Constant constant = Constant.of(value);
        assertNull(constant.getLabel(), "getLabel for Constant should return null.");
        assertEquals(value, constant.getValue(null), "getValue should return the constant's value.");
    }
}
