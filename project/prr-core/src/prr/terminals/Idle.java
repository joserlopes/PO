package prr.terminals;

import prr.exceptions.TerminalAlreadyIdleException;

public class Idle extends TerminalState {

    public Idle(Terminal terminal) {
        super(terminal, "IDLE");
    }

    public void toSilent() {
        getTerminal().setState(new Silent(getTerminal()));
    }

    public void toBusy() {
        getTerminal().setState(new Busy(getTerminal(), this));
    }

    public void toOff() {
        getTerminal().setState(new Off(getTerminal()));
    }

    public void toIdle() throws TerminalAlreadyIdleException {
        throw new TerminalAlreadyIdleException();
    }

    public void toPrevious() {
    }
}
