package prr.visits;

import prr.terminals.BasicTerminal;
import prr.terminals.FancyTerminal;

public interface TerminalVisitor {

    void visitBasicTerminal(BasicTerminal terminal, boolean last);

    void visitFancyTerminal(FancyTerminal terminal, boolean last);
}
