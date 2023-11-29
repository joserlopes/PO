package prr.clients;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import prr.communications.Communication;
import prr.exceptions.NotificationsAlreadyDisabledException;
import prr.exceptions.NotificationsAlreadyEnabledException;
import prr.notifications.Notification;
import prr.notifications.DeliveryMethod;
import prr.terminals.Terminal;
import prr.visits.ClientVisitor;

public class Client implements Serializable {

    private static final long serialVersionUID = 202208091753L;

    private String _key;

    private String _name;

    private int _taxId;

    private Map<String, Terminal> _terminals = new TreeMap<>();

    private List<Notification> _notifications = new ArrayList<>();

    private ClientType _type = new NormalClient(this);

    private String _notificationsEnabled = "YES";

    private DeliveryMethod _deliveryMethod = new DefaultDelivery();

    private int _voiceCommsStreak = 0;
    private int _videoCommsStreak = 0;

    private long _payments = 0;
    private int _debts = 0;

    public Client(String key, String name, int taxId) {
        _key = key;
        _name = name;
        _taxId = taxId;
    }

    private class DefaultDelivery implements DeliveryMethod, Serializable {
        public void deliver(Notification not) {
            if (!_notifications.contains(not))
                _notifications.add(not);
        }
    }

    public String getKey() {
        return _key;
    }

    public String getName() {
        return _name;
    }

    public int getTaxId() {
        return _taxId;
    }

    public int getNumberOfTerminals() {
        return _terminals.size();
    }

    public Map<String, Terminal> getTerminals() {
        return _terminals;
    }

    public Map<Integer, Communication> getCommsSent() {
        Map<Integer, Communication> _comms = new TreeMap<>();
        for (Terminal terminal : _terminals.values()) {
            for (Communication comm : terminal.getCommsSent().values())
                _comms.put(comm.getId(), comm);
        }
        return _comms;
    }

    public Map<Integer, Communication> getCommsReceived() {
        Map<Integer, Communication> _comms = new TreeMap<>();
        for (Terminal terminal : _terminals.values()) {
            for (Communication comm : terminal.getCommsReceived().values())
                _comms.put(comm.getId(), comm);
        }
        return _comms;
    }

    public void accept(ClientVisitor visitor, boolean last) {
        visitor.visitClient(this, last);
    }

    public void addTerminal(String key, Terminal terminal) {
        _terminals.put(key, terminal);
    }

    public void setType(ClientType type) {
        _type = type;
    }

    public void upgrade() {
        _type.upgrade();
    }

    public void downgrade() {
        _type.downgrade();
    }

    public void downgradeTwice() {
        _type.downgradeTwice();
    }

    public int textCommPrice(int nCharacters) {
        return _type.textCommPrice(nCharacters);
    }

    public int voiceCommPrice(int duration) {
        return _type.voiceCommPrice(duration);
    }

    public int videoCommPrice(int duration) {
        return _type.videoCommPrice(duration);
    }

    public ClientType getType() {
        return _type;
    }

    public String getNotificationsEnabled() {
        return _notificationsEnabled;
    }

    public void enableNofitications() throws NotificationsAlreadyEnabledException {
        if (_notificationsEnabled.equals("YES"))
            throw new NotificationsAlreadyEnabledException();
        _notificationsEnabled = "YES";
    }

    public void disableNofitications() throws NotificationsAlreadyDisabledException {
        if (_notificationsEnabled.equals("NO"))
            throw new NotificationsAlreadyDisabledException();
        _notificationsEnabled = "NO";
    }

    public void performPayment(long price) {
        _payments += price;
        _debts -= price;
        assertPayments();
    }

    public void addDebt(int price) {
        _debts += price;
    }

    public long getPayments() {
        return _payments;
    }

    public int getDebts() {
        return _debts;
    }

    public long getBalance() {
        return _payments - _debts;
    }

    public void assertPayments() {
        _type.assertPayments(getBalance());
    }

    public void resetVideoStreak() {
        _videoCommsStreak = 0;
    }

    public void increaseVideoStreak() {
        if (_type.getClientType().equals("GOLD"))
            _videoCommsStreak++;
    }

    public void resetVoiceStreak() {
        _voiceCommsStreak = 0;
    }

    public void increaseVoiceStreak() {
        if (_type.getClientType().equals("PLATINUM"))
            _voiceCommsStreak++;
    }

    public int getVoiceStreak() {
        return _voiceCommsStreak;
    }

    public int getVideoStreak() {
        return _videoCommsStreak;
    }

    public void assertComms() {
        _type.assertComms(getBalance());
    }

    public void deliverNotification(Notification not) {
        deliverNotification(not, _deliveryMethod);
    }

    public void deliverNotification(Notification not, DeliveryMethod dm) {
        dm.deliver(not);
    }

    public void clearNotifications() {
        _notifications.clear();
    }

    public Collection<Notification> getNotifications() {
        return _notifications;
    }

    public void setDeliveryMethod(DeliveryMethod dm) {
        _deliveryMethod = dm;
    }

}
