package prr.clients;

import java.io.Serializable;

public abstract class ClientType implements Serializable {

    private Client _client;
    private String _type;

    public ClientType(Client client, String type) {
        _client = client;
        _type = type;
    }

    public Client getClient() {
        return _client;
    }

    public String getClientType() {
        return _type;
    }

    public abstract void upgrade();

    public abstract void downgrade();

    public abstract void downgradeTwice();

    public abstract void assertPayments(long balance);

    public abstract void assertComms(long balance);

    public abstract int textCommPrice(int nCharacters);

    public abstract int voiceCommPrice(int duration);

    public abstract int videoCommPrice(int duration);

}
