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
import de.metalcon.sdd.error.InvalidAttrNameException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;

public class SddTest {

    private Sdd sdd;
    
    @Before
    public void setUp()
    throws IOException, InvalidConfigException, InvalidAttrNameException {
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
        
        MetaEntity entity3 = new MetaEntity();
        entity3.addAttr("attr3", new MetaType("entity1[]"));
        MetaEntityOutput entity3_detail0 = new MetaEntityOutput();
        entity3_detail0.addOattr("attr3", "detail1");
        entity3.addOutput("detail0", entity3_detail0);
        config.addEntity("entity3", entity3);
        
        MetaEntity cross1 = new MetaEntity();
        cross1.addAttr("title", new MetaType("String"));
        cross1.addAttr("elem", new MetaType("cross2"));
        MetaEntityOutput cross1_detail0 = new MetaEntityOutput();
        cross1_detail0.addOattr("title", "");
        cross1_detail0.addOattr("elem",  "detail1");
        cross1.addOutput("detail0", cross1_detail0);
        MetaEntityOutput cross1_detail1 = new MetaEntityOutput();
        cross1_detail1.addOattr("title", "");
        cross1.addOutput("detail1", cross1_detail1);
        config.addEntity("cross1", cross1);
        
        MetaEntity cross2 = new MetaEntity();
        cross2.addAttr("title", new MetaType("String"));
        cross2.addAttr("elem", new MetaType("cross1"));
        MetaEntityOutput cross2_detail0 = new MetaEntityOutput();
        cross2_detail0.addOattr("title", "");
        cross2_detail0.addOattr("elem",  "detail1");
        cross2.addOutput("detail0", cross1_detail0);
        MetaEntityOutput cross2_detail1 = new MetaEntityOutput();
        cross2_detail1.addOattr("title", "");
        cross2.addOutput("detail1", cross1_detail1);
        config.addEntity("cross2", cross2);
        
        sdd = new Sdd(config);
    }
    
    @After
    public void tearDown()
    throws IOException {
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
    public void testReadNonExistingId()
    throws InvalidDetailException {
        assertNull(sdd.readEntity(1234L, "detail0"));
    }
    
    @Test
    public void testUpdateInvalidType()
    throws InvalidAttrException {
        try {
            sdd.updateEntity(1234L, "", new HashMap<String, String>());
            fail("invalid type wasn't caught");
        } catch (InvalidTypeException e) {
        }
    }
    
    @Test
    public void testCreateRead()
    throws InvalidDetailException, InvalidTypeException, InvalidAttrException, IOException {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("attr0", "YOLO");
        sdd.updateEntity(1L, "entity0", attrs);
        sdd.waitUntilQueueEmpty();
        assertNotNull(sdd.readEntity(1L, "detail0"));
    }
    
    @Test
    public void testCreateCrossReference()
    throws InvalidTypeException, InvalidAttrException, InvalidDetailException, IOException {
        Map<String, String> cross1 = new HashMap<String, String>();
        cross1.put("title", "foo");
        Map<String, String> cross1n = new HashMap<String, String>();
        cross1n.put("title", "FOO");
        Map<String, String> cross2 = new HashMap<String, String>();
        cross2.put("title", "bar");
        
        sdd.updateEntity(51L, "cross1", cross1);
        sdd.updateEntity(52L, "cross2", cross2);
        sdd.updateRelationship(51L, "cross1", "elem", 52L);
        sdd.waitUntilQueueEmpty();
        sdd.updateRelationship(52L, "cross2", "elem", 51L);
        sdd.waitUntilQueueEmpty();
        sdd.updateEntity(51L, "cross1", cross1n);
        sdd.waitUntilQueueEmpty();
        
        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(51L, "detail0")));
        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(52L, "detail0")));
    }
    
//    @Test
//    public void testUpdateDependencies()
//    throws InvalidTypeException, InvalidAttrException, InvalidDetailException {
//        Map<String, Object> entity1 = new HashMap<String, Object>();
//        entity1.put("attr2", "FOO");
//        sdd.updateEntity(101L, "entity1", entity1);
//        Map<String, Object> entity0 = new HashMap<String, Object>();
//        entity0.put("attr1", 101L);
//        sdd.updateEntity(100L, "entity0", entity0);
//        sdd.waitUntilQueueEmpty();
//        
//        Map<String, Object> entity1_new = new HashMap<String, Object>();
//        entity1_new.put("attr2", "BAR");
//        sdd.updateEntity(101L, "entity1", entity1_new);
//        sdd.waitUntilQueueEmpty();
////        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(100L, "detail0")));
////        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(101L, "detail1")));
//    }
//    
//    @Test
//    public void testCrossReference()
//    throws InvalidTypeException, InvalidAttrException, InvalidDetailException {
//        Map<String, Object> cross1 = new HashMap<String, Object>();
//        cross1.put("title", "foo");
//        sdd.updateEntity(51L, "cross1", cross1);
//        Map<String, Object> cross2 = new HashMap<String, Object>();
//        cross2.put("title", "bar");
//        sdd.updateEntity(52L, "cross2", cross2);
//        Map<String, Object> cross1m = new HashMap<String, Object>();
//        cross1m.put("elem", 52L);
//        sdd.updateEntity(51L, "cross1", cross1m);
//        Map<String, Object> cross2m = new HashMap<String, Object>();
//        cross2m.put("elem", 51L);
//        sdd.updateEntity(52L, "cross2", cross2m);
//        sdd.waitUntilQueueEmpty();
//        
//        Map<String, Object> cross1n = new HashMap<String, Object>();
//        cross1n.put("title", "FOO");
//        sdd.updateEntity(51L, "cross1", cross1n);
//        sdd.waitUntilQueueEmpty();
//        
//        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(51L, "detail0")));
//        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(52L, "detail0")));
//        
//    }
//    
//    @Test
//    public void testCreateChild()
//    throws InvalidTypeException, InvalidAttrException, InvalidDetailException {
//        Map<String, Object> attrs_11 = new HashMap<String, Object>();
//        attrs_11.put("attr2", "I'm 11");
//        sdd.updateEntity(11L, "entity1", attrs_11);
//        Map<String, Object> attrs_12 = new HashMap<String, Object>();
//        attrs_12.put("attr2", "I'm 12");
//        sdd.updateEntity(12L, "entity1", attrs_12);
//        Map<String, Object> attrs_13 = new HashMap<String, Object>();
//        attrs_13.put("attr2", "I'm 13");
//        sdd.updateEntity(13L, "entity1", attrs_13);
//        
//        Map<String, Object> attrs_01 = new HashMap<String, Object>();
//        attrs_01.put("attr0", "I'm 01");
//        attrs_01.put("attr1", 11L);
//        sdd.updateEntity(21L, "entity0", attrs_01);
//        
//        Map<String, Object> attrs_31 = new HashMap<String, Object>();
//        attrs_31.put("attr3", new long[]{11L, 12L, 13L});
//        sdd.updateEntity(31L, "entity3", attrs_31);
//        
//        sdd.waitUntilQueueEmpty();
//        
////        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(21L, "detail0")));
////        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(31L, "detail0")));
//    }
    
}
