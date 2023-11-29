package prr.terminals;

import prr.visits.TerminalVisitor;

public class BasicTerminal extends Terminal {

    public BasicTerminal(String key, String state, String type) {
        super(key, state, type);
    }

    public void accept(TerminalVisitor visitor, boolean last) {
        visitor.visitBasicTerminal(this, last);
    }
}
