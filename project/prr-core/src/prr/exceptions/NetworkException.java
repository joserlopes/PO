package prr.exceptions;

/**
 * Basic exception class for network operations.
 */
public abstract class NetworkException extends Exception {

    /** Class serial number. */
    private static final long serialVersionUID = 202208091753L;

    public NetworkException() {
        super("(empty message)");
    }

    /** @param message */
    public NetworkException(String message) {
        super(message);
    }

}
