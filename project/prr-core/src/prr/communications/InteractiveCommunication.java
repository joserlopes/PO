package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommunicationVisitor;

public abstract class InteractiveCommunication extends Communication {

    public InteractiveCommunication(int id, Terminal to, Terminal from) {
        super(id, to, from);
    }

    public abstract int getPrice();

    public abstract int getUnits();

    public abstract void communicationEnded(int duration);

    public abstract void accept(CommunicationVisitor visitor, boolean last);

}
