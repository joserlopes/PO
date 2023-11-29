package prr.exceptions;

public class DestinationIsBusyException extends NetworkException {
    private static final long serialVersionUID = 202208091753L;
    private final String _key;

    /** @param key */
    public DestinationIsBusyException(String key) {
        _key = key;
    }

    /** @return the key */
    public String getKey() {
        return _key;
    }

}
