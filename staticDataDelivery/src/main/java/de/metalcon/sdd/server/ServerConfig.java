package de.metalcon.sdd.server;

public class ServerConfig {
    
    private String kvDbPath;
    
    private String graphDbPath;
    
    public ServerConfig() {
        kvDbPath = "/usr/share/sdd/leveldb";
        graphDbPath = "/usr/share/sdd/neo4j";
    }
    
    public ServerConfig(String kvDbPath, String graphDbPath) {
        this.kvDbPath = kvDbPath;
        this.graphDbPath = graphDbPath;
    }

    public String getKvDbPath() {
        return kvDbPath;
    }
    
    public String getGraphDbPath() {
        return graphDbPath;
    }
    
}
