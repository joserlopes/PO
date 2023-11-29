package prr.app.visitors;

import prr.visits.TerminalVisitor;

import prr.terminals.Terminal;

import java.lang.reflect.Array;
import java.util.Collection;

import prr.terminals.BasicTerminal;
import prr.terminals.FancyTerminal;

public class RenderTerminal implements TerminalVisitor {

    private String _rendering = "";

    private String renderFields(Terminal terminal) {
        int i;
        int friendsCount = 0;
        int numberOfFriends = terminal.getFriends().size();
        _rendering = terminal.getKey() + "|" + terminal.getClient().getKey() + "|"
                + terminal.getState().getTerminalState() + "|" + terminal.getPayments() + "|"
                + terminal.getDebts();
        if (terminal.getFriends().size() != 0)
            _rendering += "|";
        for (Terminal friend : terminal.getFriends()) {
            friendsCount++;
            _rendering += friend.getKey() + (friendsCount == numberOfFriends ? "\n" : ",");
        }
        return _rendering;
    }

    public void visitBasicTerminal(BasicTerminal terminal, boolean last) {
        if (terminal.getKey() == null)
            return;
        _rendering += "BASIC" + "|" + renderFields(terminal) + (last ? "" : "\n");
    }

    public void visitFancyTerminal(FancyTerminal terminal, boolean last) {
        if (terminal.getKey() == null)
            return;
        _rendering += "FANCY" + "|" + renderFields(terminal) + (last ? "" : "\n");
    }

    public String toString() {
        return _rendering;
    }
}
