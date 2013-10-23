package de.metalcon.sdd.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Config implementation where directories are located in a temporary location.
 * 
 * Primary use of this class is for testing.
 * 
 * Temporary directories are not deleted on shutdown. You will have to do that
 * manually. Most likely the directory will be located in /tmp
 */
public class TempConfig extends Config {
    
    /**
     * @throws IOException  If no temporary directory could be created.
     */
    public TempConfig() throws IOException {
        Path tmpDir = Files.createTempDirectory("sddTest");
        
        Path leveldb = tmpDir.resolve("leveldb");
        leveldb.toFile().mkdir();
        leveldbPath = leveldb.toString();
        
        Path neo4j = tmpDir.resolve("neo4j");
        neo4j.toFile().mkdir();
        neo4jPath = neo4j.toString();
        tmpDir.toFile().deleteOnExit();
        
        details     = new HashSet<String>();
        entities    = new HashMap<String, MetaEntity>();
    }
    
}
