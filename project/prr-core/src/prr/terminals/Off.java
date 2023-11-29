package prr.terminals;

import java.util.Collection;

import prr.exceptions.TerminalAlreadyOffException;
import prr.notifications.Notification;
import prr.notifications.OffToSilent;
import prr.notifications.OffToIdle;
import prr.communications.Communication;
import prr.clients.Client;

public class Off extends TerminalState {

    public Off(Terminal terminal) {
        super(terminal, "OFF");
    }

    public void toIdle() {
        Terminal terminal = getTerminal();
        if (terminal.communicationHasFailed()) {
            Notification not = new OffToIdle("O2I", terminal);
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
        Terminal terminal = getTerminal();
        if (terminal.communicationHasFailed()) {
            Notification not = new OffToSilent("O2S", terminal);
            Collection<Communication> comms = terminal.getCommsFailed();
            for (Communication comm : comms) {
                Client client = comm.getClientSender();
                client.deliverNotification(not);
            }
            terminal.clearCommsFailed();
        }
        terminal.setState(new Silent(terminal));
    }

    public void toBusy() {
    }

    public void toOff() throws TerminalAlreadyOffException {
        throw new TerminalAlreadyOffException();
    }

    public void toPrevious() {
    }
}
