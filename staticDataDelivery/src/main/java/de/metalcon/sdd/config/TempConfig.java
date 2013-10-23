package de.metalcon.sdd.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        super();
        
        Path tmpDir = Files.createTempDirectory("sddTest");
        
        Path leveldb = tmpDir.resolve("leveldb");
        leveldb.toFile().mkdir();
        setLeveldbPath(leveldb.toString());
        
        
        Path neo4j = tmpDir.resolve("neo4j");
        neo4j.toFile().mkdir();
        setNeo4jPath(neo4j.toString());
    }
    
}
