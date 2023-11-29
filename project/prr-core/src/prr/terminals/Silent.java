package prr.terminals;

import java.util.Collection;

import prr.exceptions.TerminalAlreadySilentException;
import prr.notifications.Notification;
import prr.notifications.SilentToIdle;
import prr.communications.Communication;
import prr.clients.Client;

public class Silent extends TerminalState {

    public Silent(Terminal terminal) {
        super(terminal, "SILENCE");
    }

    public void toIdle() {
        Terminal terminal = getTerminal();
        if (terminal.communicationHasFailed()) {
            Notification not = new SilentToIdle("S2I", terminal);
            Collection<Communication> comms = terminal.getCommsFailed();
            for (Communication comm : comms) {
                Client client = comm.getClientSender();
                client.deliverNotification(not);
            }
            terminal.clearCommsFailed();
        }
        terminal.setState(new Idle(terminal));
    }

    public void toBusy() {
        getTerminal().setState(new Busy(getTerminal(), this));
    }

    public void toOff() {
        getTerminal().setState(new Off(getTerminal()));
    }

    public void toSilent() throws TerminalAlreadySilentException {
        throw new TerminalAlreadySilentException();
    }

    public void toPrevious() {
    }

}
