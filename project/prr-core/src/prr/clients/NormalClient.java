package prr.clients;

public class NormalClient extends ClientType {

    public NormalClient(Client client) {
        super(client, "NORMAL");
    }

    public void upgrade() {
        getClient().setType(new GoldClient(getClient()));
    }

    public void downgrade() {
    }

    public void downgradeTwice() {
    }

    public int textCommPrice(int nCharacters) {
        if (nCharacters < 50)
            return 10;
        else if (nCharacters < 100)
            return 16;
        else
            return 2 * nCharacters;
    }

    public int voiceCommPrice(int duration) {
        return 20 * duration;
    }

    public int videoCommPrice(int duration) {
        return 30 * duration;
    }

    public void assertPayments(long balance) {
        if (balance > 500)
            upgrade();
    }

    public void assertComms(long balance) {
    }
}
