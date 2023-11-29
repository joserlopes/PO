package prr.notifications;

import java.io.Serializable;

import prr.terminals.Terminal;

public abstract class Notification implements Serializable {

    private String _type;

    private Terminal _terminal;

    public Notification(String type, Terminal terminal) {
        _type = type;
        _terminal = terminal;
    }

    public String getType() {
        return _type;
    }

    public String getTerminalKey() {
        return _terminal.getKey();
    }

}
