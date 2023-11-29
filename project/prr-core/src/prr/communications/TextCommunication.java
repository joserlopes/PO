package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommunicationVisitor;

public class TextCommunication extends Communication {

    private String _message;

    public TextCommunication(int id, Terminal to, Terminal from, String message) {
        super(id, to, from);
        _message = message;
    }

    public int getPrice() {
        return getClientSender().textCommPrice(_message.length());
    }

    public int getUnits() {
        return _message.length();
    }

    public void accept(CommunicationVisitor visitor, boolean last) {
        visitor.visitTextCommunication(this, last);
    }
}