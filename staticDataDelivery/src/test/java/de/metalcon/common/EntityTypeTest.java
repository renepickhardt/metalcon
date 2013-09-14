package de.metalcon.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class EntityTypeTest {

    @Test
    public void testStringEnumConversion() {
        // assume conversion is correct if round trip works
        for (EntityType detail : EntityType.values())
            assertEquals(detail,
                    EntityType.stringToEnum(EntityType.enumToString(detail)));
    }

}
