package de.metalcon.sdd.error;

public class InvalidReferenceException extends Exception {

    private static final long serialVersionUID = -6802166774771080639L;
    
    public InvalidReferenceException(String message) {
        super(message);
    }

}
