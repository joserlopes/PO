package prr.app.visitors;

import prr.visits.CommunicationVisitor;
import prr.communications.Communication;
import prr.communications.TextCommunication;
import prr.communications.VoiceCommunication;
import prr.communications.VideoCommunication;

public class RenderCommunication implements CommunicationVisitor {

    private String _rendering = "";

    private String renderFields(Communication comm) {
        _rendering = comm.getId() + "|" + comm.getSender().getKey() + "|" + comm.getReceiver().getKey() + "|";
        if (comm.getCommunicationState().equals("ONGOING"))
            _rendering += 0 + "|" + 0 + "|" + "ONGOING";
        else
            _rendering += comm.getUnits() + "|" + comm.getCost() + "|" + comm.getCommunicationState();
        return _rendering;
    }

    public void visitTextCommunication(TextCommunication comm, boolean last) {
        _rendering += "TEXT" + "|" + renderFields(comm) + (last ? "" : "\n");
    }

    public void visitVoiceCommunication(VoiceCommunication comm, boolean last) {
        _rendering += "VOICE" + "|" + renderFields(comm) + (last ? "" : "\n");
    }

    public void visitVideoCommunication(VideoCommunication comm, boolean last) {
        _rendering += "VIDEO" + "|" + renderFields(comm) + (last ? "" : "\n");
    }

    public String toString() {
        return _rendering;
    }
}
