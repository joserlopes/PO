package prr.clients;

public class GoldClient extends ClientType {

    public GoldClient(Client client) {
        super(client, "GOLD");
    }

    public void upgrade() {
        getClient().setType(new PlatinumClient(getClient()));
    }

    public void downgrade() {
        getClient().setType(new NormalClient(getClient()));
        getClient().resetVideoStreak();
    }

    public void downgradeTwice() {
    }

    public int textCommPrice(int nCharacters) {
        if (nCharacters < 100)
            return 10;
        else
            return 2 * nCharacters;
    }

    public int voiceCommPrice(int duration) {
        return 10 * duration;
    }

    public int videoCommPrice(int duration) {
        return 20 * duration;
    }

    public void assertPayments(long balance) {
        if (balance < 0)
            downgrade();
    }

    public void assertComms(long balance) {
        if (getClient().getVideoStreak() == 5 && balance > 0)
            upgrade();
    }
}
