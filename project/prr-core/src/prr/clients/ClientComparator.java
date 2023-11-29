package prr.clients;

import java.io.Serializable;

import java.util.Comparator;

public class ClientComparator implements Comparator<String>, Serializable {
    public int compare(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
}
