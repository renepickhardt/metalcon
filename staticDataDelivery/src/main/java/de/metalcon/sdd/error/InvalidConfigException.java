package de.metalcon.sdd.error;

public class InvalidConfigException extends Exception {

    private static final long serialVersionUID = 8829689568298152661L;

    public InvalidConfigException(String message) {
        super(message);
    }
    
    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
