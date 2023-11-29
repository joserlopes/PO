package prr.clients;

public class PlatinumClient extends ClientType {

    public PlatinumClient(Client client) {
        super(client, "PLATINUM");
    }

    public void upgrade() {
    }

    public void downgrade() {
        getClient().setType(new GoldClient(getClient()));
    }

    public void downgradeTwice() {
        getClient().setType(new NormalClient(getClient()));
    }

    public int textCommPrice(int nCharacters) {
        if (nCharacters < 50)
            return 0;
        else
            return 4;
    }

    public int voiceCommPrice(int duration) {
        return 10 * duration;
    }

    public int videoCommPrice(int duration) {
        return 10 * duration;
    }

    public void assertPayments(long balance) {
        if (balance < 0)
            downgradeTwice();
    }

    public void assertComms(long balance) {
        if (getClient().getVoiceStreak() == 2 && balance > 0)
            downgrade();
    }
}
