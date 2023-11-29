package prr.clients;

import java.io.Serializable;

import java.util.Comparator;

public class ClientDebtsComparator implements Comparator<Client>, Serializable {
    public int compare(Client c1, Client c2) {
        return c2.getDebts() - c1.getDebts();
    }
}
