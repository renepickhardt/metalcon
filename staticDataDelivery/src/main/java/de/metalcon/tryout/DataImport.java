package de.metalcon.tryout;

import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.json.simple.JSONValue;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.config.FileConfig;
import de.metalcon.sddServer.server.GraphRelTypes;

public class DataImport {
    final private static String sshHost  = "141.26.71.189";
    final private static String sshUser  = "metalcon";
    final private static String sshPass  = "rockpalast";
    final private static String dbUser   = "metalcon";
    final private static String dbPass   = "rockpalast";
    final private static String dbName   = "metalcon";
    final private static String dbDriver = "com.mysql.jdbc.Driver";
    
    private FileConfig config;
    
    private DB jsonDb;
    
    private GraphDatabaseService entityGraph;
    
    private Map<Muid, Node> entityGraphMuidIndex;
    
    private String generateJson(Node entityNode, String detail) {
        if (entityNode == null)
            return null;
        
        String id = (String) entityNode.getProperty("muid");
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", id);
        json.put("type", (String) entityNode.getProperty("type"));
        switch ((String) entityNode.getProperty("type")) {
            case "City":
                json.put("name", (String) entityNode.getProperty("name"));
                break;
                
            case "Band":
                json.put("name", (String) entityNode.getProperty("name"));
                if (detail.equals("all")) {
                    json.put("city",
                            new JsonString(generateJson(entityGraphMuidIndex.get(new Muid((String)
                                     entityNode.getProperty("city"))), "title")));
                    List<JsonString> records = new LinkedList<JsonString>();
                    for (String record : ((String) entityNode
                            .getProperty("records")).split(",")) {
                        records.add(new JsonString(generateJson(
                                entityGraphMuidIndex.get(new Muid(record)), "title")));
                    }
                    json.put("records", records);
                    json.put("posts", new LinkedList<String>());
                }
                break;
                
            case "Record":
                json.put("name", (String) entityNode.getProperty("name"));
                if (detail.equals("all"))
                    json.put("band",
                            new JsonString(generateJson(entityGraphMuidIndex.get(new Muid((String)
                                     entityNode.getProperty("band"))), "title")));
                break;
                
            default:
                System.out.println("entity without type");
                throw new RuntimeException();
        }
        return JSONValue.toJSONString(json);
    }
    
    public DataImport() {
//      Server s = new Server();
//      s.start();
      
//      UpdateRequest r;
        
        System.out.println("--------------------");
        System.out.println("Loading:");
      
        System.out.println("Loading config...");
        config = new FileConfig();
        
        System.out.println("Loading jsonDb (LevelDB)...");
        Options options = new Options();
        options.createIfMissing(true);
        try {
            jsonDb = factory.open(new File(config.getLeveldbPath()),
                                  options);
        } catch(IOException e) {
            throw new RuntimeException();
        }
        
        System.out.println("Loading entityGraph (Neo4j)...");
        entityGraph = new GraphDatabaseFactory().newEmbeddedDatabase(config
                .getNeo4jPath());
        entityGraphMuidIndex = new HashMap<Muid, Node>();
        
        try {
            int lport = 4321;
            int rport = 3306;
            String rhost = "localhost";

            Session session = null;
            Connection conn = null;
            try {
                System.out.println("Connecting to SSH...");
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");

                JSch jsch = new JSch();
                session = jsch.getSession(sshUser, sshHost, 22);
                session.setPassword(sshPass);
                session.setConfig(sshConfig);
                session.connect();
                session.setPortForwardingL(lport, rhost, rport);
                
                System.out.println("Connect to MySQL...");
                Class.forName(dbDriver);
                conn = DriverManager.getConnection("jdbc:mysql://" + rhost
                        + ":" + lport + "/" + dbName, dbUser, dbPass);

                System.out.println("--------------------");

                String cityCountQuery =
                        "select count(*) from City;";
                String bandCountQuery =
                        "select count(*) from Band;";
                String recordCountQuery =
                        "select count(*) from Album;";
                
                String cityQuery =
                        "select\n" +
                        "    ID as cityId,\n" +
                        "    Name as cityName\n" +
                        "from\n" +
                        "    City;";
                
                String bandQuery =
                        "select\n" +
                        "    ID as bandId,\n" +
                        "    Name as bandName,\n" +
                        "    City_ID as cityId\n" +
                        "from\n" +
                        "    Band;";
                
                String recordQuery =
                        "select\n" +
                        "    a.ID as recordID,\n" +
                        "    a.Name as recordName,\n" +
                        "    ba.Band_ID as bandId\n" +
                        "from\n" +
                        "    Album a join\n" +
                        "    BandAlbum ba\n" +
                        "where\n" +
                        "    a.ID = ba.Album_ID;";
                
                Statement stmt;
                ResultSet rs;
                Transaction tx;

                stmt = conn.createStatement();
                rs   = stmt.executeQuery(cityCountQuery);
                rs.next();
                int cityCount = rs.getInt(1);
                rs.close();
                stmt.close();
                System.out.println("# Cities: " + cityCount);
                stmt = conn.createStatement();
                rs   = stmt.executeQuery(bandCountQuery);
                rs.next();
                int bandCount = rs.getInt(1);
                rs.close();
                stmt.close();
                System.out.println("# Bands: " + bandCount);
                stmt = conn.createStatement();
                rs   = stmt.executeQuery(recordCountQuery);
                rs.next();
                int recordCount = rs.getInt(1);
                rs.close();
                stmt.close();
                System.out.println("# Records: " + recordCount);
                
                System.out.println("--------------------");
                System.out.println("Inserting into Graph:");
                
                stmt = conn.createStatement();
                rs   = stmt.executeQuery(cityQuery);
                tx   = entityGraph.beginTx();
                try {
                    while (rs.next()) {
                        int    cityId   = rs.getInt("cityId");
                        String cityName = rs.getString("cityName");
                        
                        Node cityNode = entityGraph.createNode();
                        cityNode.setProperty("muid", "city" + cityId);
                        cityNode.setProperty("type", "City");
                        cityNode.setProperty("name", cityName);
                        entityGraphMuidIndex.put(new Muid("city" + cityId),
                                                 cityNode);
                        
                        if (rs.getRow() % 1000 == 0) {
                            System.out.println("Inserting Cities... " +
                                               rs.getRow() + "/" + cityCount);
                            tx.success();
                            tx.finish();
                            tx = entityGraph.beginTx();
                        }
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }
                rs.close();
                stmt.close();
                System.out.println("Inserting Cities... Done.");
                
                stmt = conn.createStatement();
                rs   = stmt.executeQuery(bandQuery);
                tx   = entityGraph.beginTx();
                try {
                    while (rs.next()) {
                        int    bandId   = rs.getInt("bandId");
                        String bandName = rs.getString("bandName");
                        int    cityId   = rs.getInt("cityId");
                        
                        Node bandNode = entityGraph.createNode();
                        bandNode.setProperty("muid", "band" + bandId);
                        bandNode.setProperty("type", "Band");
                        bandNode.setProperty("name", bandName);
                        bandNode.setProperty("city", "city" + cityId);
                        bandNode.setProperty("records", "");
                        bandNode.setProperty("posts", "");
                        entityGraphMuidIndex.put(new Muid("band" + bandId),
                                                 bandNode);
                        
                        if (rs.getRow() % 1000 == 0) {
                            System.out.println("Inserting Bands... " +
                                               rs.getRow() + "/" + bandCount);
                            tx.success();
                            tx.finish();
                            tx = entityGraph.beginTx();
                        }
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }
                rs.close();
                stmt.close();
                
                stmt = conn.createStatement();
                rs   = stmt.executeQuery(recordQuery);
                tx   = entityGraph.beginTx();
                try {
                    while (rs.next()) {
                        int    recordId   = rs.getInt("recordId");
                        String recordName = rs.getString("recordName");
                        int    bandId     = rs.getInt("bandId");
                        
                        Node recordNode = entityGraph.createNode();
                        recordNode.setProperty("muid", "record" + recordId);
                        recordNode.setProperty("type", "Record");
                        recordNode.setProperty("name", recordName);
                        recordNode.setProperty("band", "band" + bandId);
                        entityGraphMuidIndex.put(new Muid("record" + recordId),
                                                 recordNode);
                        
                        if (rs.getRow() % 1000 == 0) {
                            System.out.println("Inserting Records... " +
                                               rs.getRow() + "/" + recordCount);
                            tx.success();
                            tx.finish();
                            tx = entityGraph.beginTx();
                        }
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }
                
                System.out.println("--------------------");
                System.out.println("Creating Dependencies:");
                
                tx = entityGraph.beginTx();
                try {
                    int i = 0;
                    for (Node entityNode : entityGraphMuidIndex.values()) {
                        String id = (String) entityNode.getProperty("muid");
                        switch ((String) entityNode.getProperty("type")) {
                            case "City":
                                break;
                                
                            case "Band":
                                Muid cityId = new Muid((String) entityNode.getProperty("city"));
                                Node cityNode = entityGraphMuidIndex.get(cityId);
                                if (cityNode != null)
                                    entityNode.createRelationshipTo(
                                            cityNode, GraphRelTypes.DEPENDS);
                                break;
                                
                            case "Record":
                                Muid bandId = new Muid((String) entityNode.getProperty("band"));
                                Node bandNode = entityGraphMuidIndex.get(bandId);
                                if (bandNode != null) {
                                    entityNode.createRelationshipTo(
                                            bandNode, GraphRelTypes.DEPENDS);
                                    bandNode.createRelationshipTo(
                                            entityNode, GraphRelTypes.DEPENDS);
                                    String records = (String) bandNode.getProperty("records");
                                    if (records.equals(""))
                                        records += id;
                                    else
                                        records += "," + id;
                                    bandNode.setProperty("records", records);
                                }
                                break;
    
                            default:
                                System.err.println("Node without Type");
                                throw new RuntimeException();
                        }
                        
                        ++i;
                        if (i % 1000 == 0)
                            System.out.println("Creating Dependencies... " +
                                        i + "/" + entityGraphMuidIndex.size());
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }
                
                System.out.println("--------------------");
                System.out.println("Filling jsonDb:");
                WriteBatch batch = jsonDb.createWriteBatch();
                try {
                    int i = 0;
                    for (Node entityNode : entityGraphMuidIndex.values()) {
                        String id = (String) entityNode.getProperty("muid");
                        batch.put(bytes(id + ":all"),
                                  bytes(generateJson(entityNode, "all")));
                        batch.put(bytes(id + ":title"),
                                  bytes(generateJson(entityNode, "title")));
                        
                        ++i;
                        if (i % 1000 == 0) {
                            System.out.println("Filling jsonDb..." +
                                    i + "/" + entityGraphMuidIndex.size());
                            jsonDb.write(batch);
                            batch = jsonDb.createWriteBatch();
                        }
                    }
                } finally {
                    try {
                        batch.close();
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }
              
//              int entityCount = 0;
//              
//              for (int i = 0; i != 50; ++i) {
//                  Statement stmt = conn.createStatement();
//                  ResultSet rs = stmt.executeQuery(
//                          "select \n" +
//                          "    b.ID   as bandId,\n" +
//                          "    b.NAME as bandName,\n" +
//                          "    c.ID as cityId,\n" +
//                          "    c.Name as cityName\n" +
//                          "from\n" +
//                          "    Band b join" +
//                          "    City c\n" +
//                          "where\n" +
//                          "    b.City_ID = c.ID\n" +
//                          "order by\n" +
//                          "    b.UserCount DESC\n" +
//                          "limit " + i*100 + "," + 100 + ";");
//                  
//                  while (rs.next()) {
//                      ++entityCount;
//                      int    bandId   = rs.getInt("bandId");
//                      String bandName = rs.getString("bandName");
//                      ++entityCount;
//                      int    cityId   = rs.getInt("cityId");
//                      String cityName = rs.getString("cityName");
//                      String records  = "";
//                      
//                      Map<String, String[]> city = new HashMap<String, String[]>();
//                      city.put("id", new String[]{"city" + cityId});
//                      city.put("type", new String[]{"City"});
//                      city.put("name", new String[]{cityName});
//                      r = new UpdateRequest(s);
//                      r.setParams(city);
//                      r.runHttp();
//                      Map<String, String[]> band = new HashMap<String, String[]>();
//                      band.put("id", new String[]{"band" + bandId});
//                      band.put("type", new String[]{"Band"});
//                      band.put("name", new String[]{bandName});
////                      band.put("city", new String[]{"city" + cityId});
//                      r = new UpdateRequest(s);
//                      r.setParams(band);
//                      r.runHttp();
//                      
//                      Statement stmt2 = conn.createStatement();
//                      ResultSet rs2 = stmt2.executeQuery(
//                              "select\n" +
//                              "    a.ID as albumId,\n" +
//                              "    a.Name as albumName\n" +
//                              "from\n" +
//                              "    Album a join\n" +
//                              "    BandAlbum ba\n" +
//                              "where\n" +
//                              "    ba.Band_ID = " + bandId + " and\n" +
//                              "    a.ID = ba.Album_ID;");
//                      while (rs2.next()) {
//                          ++entityCount;
//                          int    albumId   = rs2.getInt("albumId");
//                          String albumName = rs2.getString("albumName");
//                          records += "," + "record" + albumId;
//                          
//                          Map<String, String[]> record = new HashMap<String, String[]>();
//                          record.put("id", new String[]{"record" + albumId});
//                          record.put("type", new String[]{"Record"});
//                          record.put("name", new String[]{albumName});
////                          record.put("band", new String[]{"band" + bandId});
//                          r = new UpdateRequest(s);
//                          r.setParams(record);
//                          r.runHttp();
//                      }
//                      rs2.close();
//                      stmt2.close();
//                      
//                      if (!records.equals(""))
//                          records = records.substring(1); // remove first comma
//                      
//                      Map<String, String[]> bandRecord = new HashMap<String, String[]>();
//                      bandRecord.put("id", new String[]{"band" + bandId});
//                      bandRecord.put("type", new String[]{"Band"});
//                      bandRecord.put("records", new String[]{records});
//                      r = new UpdateRequest(s);
//                      r.setParams(band);
//                      r.runHttp();
//                  }
//                  rs.close();
//                  stmt.close();
//              }
//              System.out.println(entityCount);
            } finally {
                if (conn != null && !conn.isClosed())
                    conn.close();

                if (session != null && session.isConnected())
                    session.disconnect();
            }
        } catch (JSchException e) {
            // TODO: handle this
            System.out.println(e);
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            // TODO: handle this (driver not found)
            System.out.println(e);
            throw new RuntimeException();
        } catch (SQLException e) {
            // TODO: handle this
            System.out.println(e);
            throw new RuntimeException();
        }
        
//      s.waitUntilQueueEmpty();
//      s.stop();
    }

    public static void main(String[] args) {
        new DataImport();
    }
    
}
