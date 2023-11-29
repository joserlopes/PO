package prr.terminals;

import java.util.Collection;

import prr.notifications.Notification;
import prr.notifications.BusyToIdle;
import prr.communications.Communication;
import prr.clients.Client;

public class Busy extends TerminalState {

    public TerminalState _previous;

    public Busy(Terminal terminal, TerminalState previous) {
        super(terminal, "BUSY");
        _previous = previous;
    }

    public void toIdle() {
        Terminal terminal = getTerminal();
        if (terminal.communicationHasFailed()) {
            Notification not = new BusyToIdle("B2I", terminal);
            Collection<Communication> comms = terminal.getCommsFailed();
            for (Communication comm : comms) {
                Client client = comm.getClientSender();
                client.deliverNotification(not);
            }
            terminal.clearCommsFailed();
        }
        terminal.setState(new Idle(terminal));
    }

    public void toSilent() {
        getTerminal().setState(new Silent(getTerminal()));
    }

    public void toBusy() {
    }

    public void toOff() {
    }

    public void toPrevious() {
        if (_previous.getTerminalState().equals("IDLE"))
            toIdle();
        else
            toSilent();
    }

}
