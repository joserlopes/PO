package prr.app.visitors;

import prr.visits.ClientVisitor;
import prr.clients.Client;
import prr.notifications.Notification;

public class RenderClient implements ClientVisitor {

    private String _rendering = "";

    private String renderFields(Client client) {
        return client.getKey() + "|" + client.getName() + "|" + client.getTaxId() + "|"
                + client.getType().getClientType() + "|" + client.getNotificationsEnabled()
                + "|"
                + client.getNumberOfTerminals() + "|" + client.getPayments() + "|" + client.getDebts();
    }

    public void visitOneClient(Client client, boolean last) {
        int notifCount = 0;
        int numberOfNotifs = client.getNotifications().size();
        if (client.getKey() == null)
            return;
        _rendering += "CLIENT" + "|" + renderFields(client);
        if (client.getNotifications().size() != 0)
            _rendering += "\n";
        for (Notification not : client.getNotifications()) {
            notifCount++;
            _rendering += not.getType() + "|" + not.getTerminalKey() + (notifCount == numberOfNotifs ? "" : "\n");
        }
        client.clearNotifications();
    }

    public void visitClient(Client client, boolean last) {
        if (client.getKey() == null)
            return;
        _rendering += "CLIENT" + "|" + renderFields(client) + (last ? "" : "\n");
    }

    public String toString() {
        return _rendering;
    }

}
