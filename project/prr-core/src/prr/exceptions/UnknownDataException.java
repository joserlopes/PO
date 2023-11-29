package prr.exceptions;

public class UnknownDataException extends Exception {

    private static final long serialVersionUID = 202208091753L;

    public UnknownDataException(String unkownData) {
        super(unkownData);
    }
}
