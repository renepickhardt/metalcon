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
import de.metalcon.sdd.config.TempConfig;
import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidAttrNameException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;

public class SddMetalconTest {

    private Sdd sdd;
    
    @Before
    public void setUp()
    throws IOException, InvalidAttrNameException, InvalidConfigException {
        Config config = new TempConfig();
        
        config.addDetail("page");
        config.addDetail("big");
        config.addDetail("small");
        
        MetaEntity band = new MetaEntity();
        band.addAttr("name",    "String");
        band.addAttr("records", "Record[]");
        MetaEntityOutput band_page = new MetaEntityOutput();
        band_page.addOattr("name", "");
        band_page.addOattr("records", "big");
        band.addOutput("page", band_page);
        MetaEntityOutput band_big = new MetaEntityOutput();
        band_big.addOattr("name", "");
        band_big.addOattr("records", "small");
        band.addOutput("big", band_big);
        MetaEntityOutput band_small = new MetaEntityOutput();
        band_small.addOattr("name", "");
        band.addOutput("small", band_small);
        config.addEntity("Band", band);
        
        MetaEntity record = new MetaEntity();
        record.addAttr("name",   "String");
        record.addAttr("band",   "Band");
        record.addAttr("tracks", "Track[]");
        MetaEntityOutput record_page = new MetaEntityOutput();
        record_page.addOattr("name", "");
        record_page.addOattr("band", "big");
        record_page.addOattr("tracks", "big");
        record.addOutput("page", record_page);
        MetaEntityOutput record_big = new MetaEntityOutput();
        record_big.addOattr("name", "");
        record_big.addOattr("band", "small");
        record_big.addOattr("tracks", "small");
        record.addOutput("big", record_big);
        MetaEntityOutput record_small = new MetaEntityOutput();
        record_small.addOattr("name", "");
        record.addOutput("small", record_small);
        config.addEntity("Record", record);
        
        MetaEntity track = new MetaEntity();
        track.addAttr("name",   "String");
        track.addAttr("band",   "Band");
        track.addAttr("record", "Record");
        MetaEntityOutput track_page = new MetaEntityOutput();
        track_page.addOattr("name", "");
        track_page.addOattr("band", "big");
        track_page.addOattr("record", "big");
        track.addOutput("page", track_page);
        MetaEntityOutput track_big = new MetaEntityOutput();
        track_big.addOattr("name", "");
        track_big.addOattr("band", "small");
        track_big.addOattr("record", "small");
        track.addOutput("big", track_big);
        MetaEntityOutput track_small = new MetaEntityOutput();
        track_small.addOattr("name", "");
        track.addOutput("small", track_small);
        config.addEntity("Track", track);
        
        sdd = new Sdd(config);
    }
    
    @After
    public void tearDown()
    throws IOException {
        sdd.close();
    }
    
    @Test
    public void testExampleSetUp()
    throws InvalidTypeException, InvalidAttrException, InvalidDetailException, IOException, InvalidAttrNameException {
        Map<String, String> ensiferum = new HashMap<String, String>();
        ensiferum.put("name", "Ensiferum");
        Map<String, String> iron = new HashMap<String, String>();
        iron.put("name", "Iron");
        Map<String, String> victorySongs = new HashMap<String, String>();
        victorySongs.put("name", "Victory Songs");
        Map<String, String> intoBattle = new HashMap<String, String>();
        intoBattle.put("name", "Into Battle");
        Map<String, String> laiLaiHei = new HashMap<String, String>();
        laiLaiHei.put("name", "Lai Lai Hei");
        Map<String, String> ahti = new HashMap<String, String>();
        ahti.put("name", "Ahti");
        
        long t1 = System.currentTimeMillis();
        
        sdd.updateEntityAttrs(31L, "Band", ensiferum);
        sdd.updateEntityAttrs(32L, "Record", iron);
        sdd.updateEntityAttrs(33L, "Record", victorySongs);
        sdd.updateEntityAttrs(34L, "Track", intoBattle);
        sdd.updateEntityAttrs(35L, "Track", laiLaiHei);
        sdd.updateEntityAttrs(36L, "Track", ahti);
        
        sdd.updateEntityRel(31L, "Band", "records", new long[]{32L, 33L});
        sdd.updateEntityRel(32L, "Record", "band", 31L);
        sdd.updateEntityRel(32L, "Record", "tracks", new long[]{34L, 35L});
        sdd.updateEntityRel(33L, "Record", "band", 31L);
        sdd.updateEntityRel(33L, "Record", "tracks", new long[]{36L});
        sdd.updateEntityRel(34L, "Track", "band", 31L);
        sdd.updateEntityRel(34L, "Track", "record", 32L);
        sdd.updateEntityRel(35L, "Track", "band", 31L);
        sdd.updateEntityRel(35L, "Track", "record", 32L);
        sdd.updateEntityRel(36L, "Track", "band", 31L);
        sdd.updateEntityRel(35L, "Track", "record", 33L);
        
        sdd.waitUntilQueueEmpty();
        
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
        
        System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(32L, "page")));
    }
    
}
