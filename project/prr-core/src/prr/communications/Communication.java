package prr.communications;

import java.io.Serializable;

import prr.terminals.Terminal;
import prr.visits.CommunicationVisitor;
import prr.clients.Client;

public abstract class Communication implements Serializable {

    private static final long serialVersionUID = 202208091753L;

    private int _id;

    private Terminal _to;
    private Terminal _from;

    private String _communcationState;

    private int _cost;

    public Communication(int id, Terminal to, Terminal from) {
        _id = id;
        _to = to;
        _from = from;
    }

    public int getId() {
        return _id;
    }

    public Terminal getReceiver() {
        return _to;
    }

    public Terminal getSender() {
        return _from;
    }

    public Client getClientSender() {
        return _from.getClient();
    }

    public Client getClientReceiver() {
        return _to.getClient();
    }

    public void communicationStarted() {
        _communcationState = "ONGOING";
    }

    public void communicationEnded() {
        _communcationState = "FINISHED";
    }

    public String getCommunicationState() {
        return _communcationState;
    }

    public void setCost(int cost) {
        _cost = cost;
    }

    public int getCost() {
        return _cost;
    }

    public abstract int getPrice();

    public abstract int getUnits();

    public abstract void accept(CommunicationVisitor visitor, boolean last);
}
