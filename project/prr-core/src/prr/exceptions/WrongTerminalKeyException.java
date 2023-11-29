package prr.exceptions;

public class WrongTerminalKeyException extends NetworkException {
    private static final long serialVersionUID = 202208091753L;

    /** The client's key. */
    private final String _key;

    /** @param key */
    public WrongTerminalKeyException(String key) {
        _key = key;
    }

    /** @return the key */
    public String getKey() {
        return _key;
    }

}
