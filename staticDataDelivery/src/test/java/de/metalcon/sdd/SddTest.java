package de.metalcon.sdd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.TempConfig;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;

public class SddTest {

    private Sdd<Long> sdd;
    
    @Before
    public void setUp() throws IOException {
        Config config = new TempConfig();
        config.addDetail("all");
        config.addDetail("title");
        sdd = new Sdd<Long>(config);
    }
    
    @After
    public void tearDown() {
        sdd.close();
    }
    
    @Test
    public void testReadInvalidDetail() {
        try {
            sdd.readEntity(1234L, "");
            fail("invalid detail wasn't caught");
        } catch(InvalidDetailException e) {
        }
    }
    
    @Test
    public void testReadNonExistingId() throws InvalidDetailException {
        assertNull(sdd.readEntity(1234L, "all"));
    }
    
    @Test
    public void testUpdateInvalidType() {
        try {
            sdd.updateEntity(1234L, "", new HashMap<String, String>());
            fail("invalid type wasn't caught");
        } catch (InvalidTypeException e) {
        }
    }
    
    @Test
    public void testCreateRead()
            throws InvalidDetailException, InvalidTypeException {
        Map<String, String> attrs = new HashMap<String, String>();
        sdd.updateEntity(1L, "", attrs);
        sdd.waitUntilQueueEmpty();
        assertNotNull(sdd.readEntity(1L, "all"));
    }

}
