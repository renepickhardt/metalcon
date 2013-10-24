package de.metalcon.sdd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.MetaEntity;
import de.metalcon.sdd.config.MetaEntityOutput;
import de.metalcon.sdd.config.MetaType;
import de.metalcon.sdd.config.TempConfig;
import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;

public class SddTest {

    private Sdd<Long> sdd;
    
    @Before
    public void setUp() throws IOException, InvalidConfigException {
        Config config = new TempConfig();
        
        config.addDetail("detail0");
        config.addDetail("detail1");
        
        MetaEntity entity0 = new MetaEntity();
        entity0.addAttr("attr0", new MetaType("String"));
        entity0.addAttr("attr1", new MetaType("entity1"));
        MetaEntityOutput entity0_detail0 = new MetaEntityOutput();
        entity0_detail0.addOattr("attr0", "");
        entity0_detail0.addOattr("attr1", "detail1");
        entity0.addOutput("detail0", entity0_detail0);
        config.addEntity("entity0", entity0);
        
        MetaEntity entity1 = new MetaEntity();
        entity1.addAttr("attr2", new MetaType("String"));
        MetaEntityOutput entity1_detail0 = new MetaEntityOutput();
        entity1.addOutput("detail0", entity1_detail0);
        MetaEntityOutput entity1_detail1 = new MetaEntityOutput();
        entity1_detail1.addOattr("attr2", "");
        entity1.addOutput("detail1", entity1_detail1);
        config.addEntity("entity1", entity1);
        
        MetaEntity entity2 = new MetaEntity();
        config.addEntity("entity2", entity2);
        
        sdd = new Sdd<Long>(config);
    }
    
    @After
    public void tearDown() throws IOException {
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
        assertNull(sdd.readEntity(1234L, "detail0"));
    }
    
    @Test
    public void testUpdateInvalidType() throws InvalidAttrException {
        try {
            sdd.updateEntity(1234L, "", new HashMap<String, String>());
            fail("invalid type wasn't caught");
        } catch (InvalidTypeException e) {
        }
    }
    
    @Test
    public void testCreateRead()
            throws InvalidDetailException, InvalidTypeException,
            InvalidAttrException {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("attr0", "YOLO");
        sdd.updateEntity(1L, "entity0", attrs);
        sdd.waitUntilQueueEmpty();
        assertNotNull(sdd.readEntity(1L, "detail0"));
        System.out.println(JsonPrettyPrinter.prettyPrintJson(
                sdd.readEntity(1L, "detail0")));
        System.out.println(JsonPrettyPrinter.prettyPrintJson(
                sdd.readEntity(1L, "detail1")));
    }

}
