package prr.exceptions;

public class UnsupportedAtDestinationException extends NetworkException {
    private static final long serialVersionUID = 202208091753L;

    private final String _key;
    private final String _type;

    public UnsupportedAtDestinationException(String key, String type) {
        _key = key;
        _type = type;
    }

    public String getKey() {
        return _key;
    }

    public String getType() {
        return _type;
    }
}
