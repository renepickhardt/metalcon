package de.metalcon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.FileConfig;
import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidAttrNameException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;

public class SddMetalconPersistentTest {

    private Sdd sdd;
    
    @Before
    public void setUp()
    throws InvalidAttrNameException, InvalidConfigException, IOException {
        Config config = new FileConfig(Paths.get("test/metalconPersistentConfig.xml"));
        sdd = new Sdd(config);
    }
    
    @After
    public void tearDown()
    throws IOException {
        sdd.close();
    }
    
    @Test
    public void testExampleSetUp()
    throws InvalidTypeException, InvalidAttrException, InvalidDetailException, IOException {
        int run = 1;
        
        if (run == 1) {
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
            
            sdd.updateEntity(31L, "Band", ensiferum);
            sdd.updateEntity(32L, "Record", iron);
            sdd.updateEntity(33L, "Record", victorySongs);
            sdd.updateEntity(34L, "Track", intoBattle);
            sdd.updateEntity(35L, "Track", laiLaiHei);
            sdd.updateEntity(36L, "Track", ahti);
            
            sdd.updateRelationship(31L, "Band", "records", new long[]{32L, 33L});
            sdd.updateRelationship(32L, "Record", "band", 31L);
            sdd.updateRelationship(32L, "Record", "tracks", new long[]{34L, 35L});
            sdd.updateRelationship(33L, "Record", "band", 31L);
            sdd.updateRelationship(33L, "Record", "tracks", new long[]{36L});
            sdd.updateRelationship(34L, "Track", "band", 31L);
            sdd.updateRelationship(34L, "Track", "record", 32L);
            sdd.updateRelationship(35L, "Track", "band", 31L);
            sdd.updateRelationship(35L, "Track", "record", 32L);
            sdd.updateRelationship(36L, "Track", "band", 31L);
            sdd.updateRelationship(35L, "Track", "record", 33L);
            
            sdd.waitUntilQueueEmpty();
            
            long t2 = System.currentTimeMillis();
            System.out.println(t2 - t1);
            
            System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(32L, "page")));
        } else if (run == 2) {
            System.out.println(JsonPrettyPrinter.prettyPrintJson(sdd.readEntity(32L, "page")));
        } else if (run == 3) {
            
        }
        
    }

}
