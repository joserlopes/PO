package prr.communications;

import java.io.Serializable;

import java.util.Comparator;

public class CommunicationComparator implements Comparator<Communication>, Serializable {
    public int compare(Communication s1, Communication s2) {
        return s1.getId() - s2.getId();
    }
}
