package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommunicationVisitor;

public class VoiceCommunication extends InteractiveCommunication {

    private int _duration;

    public VoiceCommunication(int id, Terminal to, Terminal from) {
        super(id, to, from);
    }

    public void accept(CommunicationVisitor visitor, boolean last) {
        visitor.visitVoiceCommunication(this, last);
    }

    public void communicationEnded(int duration) {
        _duration = duration;
        communicationEnded();
    }

    public int getUnits() {
        return _duration;
    }

    public int getPrice() {
        return getClientSender().voiceCommPrice(_duration);
    }
}