package de.metalcon.sdd.generator;

public class Detail {

    private String name;
    
    public Detail(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getConstname() {
        return Detail.toConstname(name);
    }

    public static String toConstname(String name) {
        return name.toUpperCase();
    }
    
}
