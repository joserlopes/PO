package prr.visits;

import prr.communications.TextCommunication;
import prr.communications.VoiceCommunication;
import prr.communications.VideoCommunication;

public interface CommunicationVisitor {
    void visitTextCommunication(TextCommunication comm, boolean last);

    void visitVoiceCommunication(VoiceCommunication comm, boolean last);

    void visitVideoCommunication(VideoCommunication comm, boolean last);
}
