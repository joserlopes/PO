package prr.terminals;

import prr.visits.TerminalVisitor;

public class FancyTerminal extends Terminal {

    private String _type = "FANCY";

    public FancyTerminal(String key, String state, String type) {
        super(key, state, type);
    }

    public void accept(TerminalVisitor visitor, boolean last) {
        visitor.visitFancyTerminal(this, last);
    }

    public String getType() {
        return _type;
    }
}
