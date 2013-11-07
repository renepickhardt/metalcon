package de.metalcon.sdd;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.FileConfig;
import de.metalcon.sdd.error.InvalidAttrNameException;
import de.metalcon.sdd.error.InvalidConfigException;

public class FileConfigTest {
    
    private Config config;
    
    @Before
    public void setUp() throws InvalidAttrNameException {
        config = new FileConfig(Paths.get("test/fileConfigTest.xml"));
    }

    @Test
    public void testLeveldbPath() {
        assertEquals("leveldbpath", config.getLeveldbPath());
    }
    
    @Test
    public void testNeo4jPath() {
        assertEquals("neo4jpath", config.getNeo4jPath());
    }
    
    @Test
    public void testDetails() {
        assertTrue(config.isValidDetail("detail0"));
        assertTrue(config.isValidDetail("detail1"));
        assertFalse(config.isValidDetail("invaliddetail"));
    }
        
    @Test
    public void testEntities() {
        assertNotNull(config.getEntity("entity0"));
        assertNotNull(config.getEntity("entity1"));
        assertNotNull(config.getEntity("entity2"));
        assertNotNull(config.getEntity("entity3"));
        assertNull(config.getEntity("invalidentity"));
    }
    
    @Test
    public void testValidate() throws InvalidConfigException, InvalidAttrNameException {
        config.validate();
    }

}
