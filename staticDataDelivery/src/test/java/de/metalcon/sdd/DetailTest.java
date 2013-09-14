package de.metalcon.sdd;

import static org.junit.Assert.*;

import org.junit.Test;

public class DetailTest {

    @Test
    public void testStringEnumConversion() {
        // assume conversion is correct if round trip works
        for (Detail detail : Detail.values())
            assertEquals(detail,
                    Detail.stringToEnum(Detail.enumToString(detail)));
    }

}
