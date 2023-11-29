package prr.terminals;

import java.io.Serializable;

import prr.exceptions.TerminalAlreadyIdleException;
import prr.exceptions.TerminalAlreadyOffException;
import prr.exceptions.TerminalAlreadySilentException;

public abstract class TerminalState implements Serializable {

    private Terminal _terminal;
    private String _state;

    public TerminalState(Terminal terminal, String state) {
        _terminal = terminal;
        _state = state;
    }

    public Terminal getTerminal() {
        return _terminal;
    }

    public String getTerminalState() {
        return _state;
    }

    public boolean equals(Object o) {
        if (o instanceof TerminalState) {
            TerminalState terminalState = (TerminalState) o;
            return _terminal.getKey().equals(terminalState._terminal.getKey()) && _state.equals(terminalState._state);
        }
        return false;
    }

    public abstract void toIdle() throws TerminalAlreadyIdleException;

    public abstract void toSilent() throws TerminalAlreadySilentException;

    public abstract void toBusy();

    public abstract void toOff() throws TerminalAlreadyOffException;

    public abstract void toPrevious();
}
