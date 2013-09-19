package de.metalcon.generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EntityClassGenerator {
    
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "data.cfg"));
            
            try {
                String line;
                
                while ((line = reader.readLine()) != null) {
                    if (line == "DETAILS")
                        break;
                }
                System.out.println("details");
                
                while ((line = reader.readLine()) != null) {
                    if (line == "ENTITIES")
                        break;
                }
                System.out.println("entities");
            } finally {
                reader.close();
            }
        } catch(FileNotFoundException e) {
            // TODO: handle this
            throw new RuntimeException(e);
        } catch(IOException e) {
            // TODO: handle this
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new EntityClassGenerator().run();
    }
    
}
