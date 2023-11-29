package prr.terminals;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import prr.Network;
import prr.clients.Client;
import prr.exceptions.TerminalAlreadyIdleException;
import prr.exceptions.TerminalAlreadyOffException;
import prr.exceptions.TerminalAlreadySilentException;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.DestinationIsOffException;
import prr.exceptions.DestinationIsSilentException;
import prr.exceptions.DestinationIsBusyException;
import prr.exceptions.NoOngoingCommunicationException;
import prr.exceptions.UnsupportedAtOriginException;
import prr.exceptions.UnsupportedAtDestinationException;
import prr.exceptions.InvalidCommunicationException;
import prr.communications.Communication;
import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;
import prr.communications.VoiceCommunication;
import prr.communications.VideoCommunication;
import prr.visits.Selector;
import prr.visits.TerminalVisitor;
import prr.visits.CommunicationVisitor;

/**
 * Abstract terminal.
 */
public abstract class Terminal implements Serializable {

	/** Serial number for serialization. */
	private static final long serialVersionUID = 202208091753L;

	private String _key;

	private Client _client; // owner

	private TerminalState _state;

	private String _type;

	private Map<String, Terminal> _friends = new TreeMap<>();

	private Map<Integer, Communication> _commsSent = new TreeMap<>();
	private Map<Integer, Communication> _commsReceived = new TreeMap<>();
	private Map<Integer, Communication> _commsPayed = new TreeMap<>();
	private List<Communication> _commsFailed = new ArrayList<>();
	private InteractiveCommunication _ongoingComm;

	private Map<Integer, Integer> _debtsToPay = new TreeMap<>();

	private long _payments = 0;
	private long _debts = 0;

	private boolean _hasCommunicated = false;
	private boolean _communicationFailed = false;

	public Terminal(String key, String state, String type) {
		_key = key;

		if (state == null)
			_state = new Idle(this);
		else
			switch (state) {
				case "ON" -> _state = new Idle(this);
				case "OFF" -> _state = new Off(this);
				case "SILENCE" -> _state = new Silent(this);
				case "BUSY" -> _state = new Busy(this, null);
			}
		_type = type;
	}

	public String getKey() {
		return _key;
	}

	public String getType() {
		return _type;
	}

	public Client getClient() {
		return _client;
	}

	public void setClient(Client client) {
		_client = client;
	}

	public Collection<Terminal> getFriends() {
		return _friends.values();
	}

	public void addFriend(String key, Network network)
			throws TerminalNotFoundException {
		if (_friends.containsKey(key) || key.equals(_key))
			return;
		Terminal terminalFriend = network.getTerminal(key);
		_friends.put(key, terminalFriend);
	}

	public void removeFriend(String key) throws TerminalNotFoundException {
		if (!_friends.containsKey(key))
			return;
		_friends.remove(key);
	}

	public abstract void accept(TerminalVisitor visitor, boolean last);

	public TerminalState getState() {
		return _state;
	}

	public void setState(TerminalState state) {
		_state = state;
	}

	public void toIdle() throws TerminalAlreadyIdleException {
		_state.toIdle();
		_communicationFailed = false;
	}

	public void toSilent() throws TerminalAlreadySilentException {
		_state.toSilent();
		_communicationFailed = false;

	}

	public void toBusy() {
		_state.toBusy();
	}

	public void toOff() throws TerminalAlreadyOffException {
		_state.toOff();
	}

	public void toPrevious() {
		_state.toPrevious();
	}

	public boolean isFriend(String key) {
		return (_friends.containsKey(key));
	}

	public void sendTextCommunication(String key, String message, Network network)
			throws TerminalNotFoundException, DestinationIsOffException {
		int id = network.getNComms();
		Terminal to = network.getTerminal(key);
		Communication comm = new TextCommunication(id, to, this, message);
		assertDestinationText(to, comm);
		int price = comm.getPrice();
		comm.setCost(price);
		_commsSent.put(id, comm);
		network.newComm(id, comm);
		to.receiveCommunication(id, comm);
		_debts += price;
		_debtsToPay.put(id, price);
		_client.addDebt(price);
		_client.assertPayments();
		_client.increaseVoiceStreak();
		_client.resetVideoStreak();
		_client.assertComms();
		comm.communicationEnded();
		_hasCommunicated = true;
		to.communicated();
	}

	public void startInteractiveCommunication(String key, String type, Network network)
			throws TerminalNotFoundException, UnsupportedAtOriginException, UnsupportedAtDestinationException,
			DestinationIsOffException, DestinationIsSilentException, DestinationIsBusyException {
		int id = network.getNComms();
		Terminal to = network.getTerminal(key);
		InteractiveCommunication comm;
		if (type.equals("VOICE"))
			comm = new VoiceCommunication(id, to, this);
		else
			comm = new VideoCommunication(id, to, this);
		assertOrigin(type);
		assertDestination(to, type, comm);
		_commsSent.put(id, comm);
		network.newComm(id, comm);
		to.receiveCommunication(id, comm);
		comm.communicationStarted();
		_ongoingComm = comm;
		to.setOngoingComm(comm);
		_hasCommunicated = true;
		to.communicated();
		toBusy();
		to.toBusy();
	}

	public long endInteractiveCommunication(int duration, Network network) throws TerminalAlreadySilentException {
		_ongoingComm.communicationEnded(duration);
		int price = _ongoingComm.getPrice();
		int id = network.getNComms();
		Terminal to = _ongoingComm.getReceiver();
		if (isFriend(to.getKey()))
			price *= 0.5;
		_ongoingComm.setCost(price);
		_debts += price;
		_debtsToPay.put(id - 1, price); // Because here the comms id has advanced 1
		toPrevious();
		to.toPrevious();
		_client.addDebt(price);
		_client.assertPayments();
		_client.increaseVideoStreak();
		_client.resetVoiceStreak();
		_client.assertComms();
		_ongoingComm = null;
		to.setOngoingComm(null);
		return price;
	}

	public void setOngoingComm(InteractiveCommunication comm) {
		_ongoingComm = comm;
	}

	public void assertOrigin(String type) throws UnsupportedAtOriginException {
		if (type.equals("VIDEO") && getType().equals("BASIC"))
			throw new UnsupportedAtOriginException(getKey(), type);
	}

	public void assertDestination(Terminal to, String type, Communication comm)
			throws UnsupportedAtDestinationException, DestinationIsOffException, DestinationIsSilentException,
			DestinationIsBusyException {
		if (type.equals("VIDEO") && to.getType().equals("BASIC"))
			throw new UnsupportedAtDestinationException(to.getKey(), type);
		if (to.getState().getTerminalState().equals("OFF")) {
			if (_client.getNotificationsEnabled().equals("YES")) {
				to.setCommunicationFailed();
				to.addCommFailed(comm);
			}

			throw new DestinationIsOffException(to.getKey());
		}
		if (to.getState().getTerminalState().equals("SILENCE")) {
			if (_client.getNotificationsEnabled().equals("YES")) {
				to.setCommunicationFailed();
				to.addCommFailed(comm);
			}

			throw new DestinationIsSilentException(to.getKey());
		}
		if (to.getState().getTerminalState().equals("BUSY") || to.getKey().equals(getKey())) {
			if (_client.getNotificationsEnabled().equals("YES")) {
				to.setCommunicationFailed();
				to.addCommFailed(comm);
			}
			throw new DestinationIsBusyException(to.getKey());
		}
	}

	public void assertDestinationText(Terminal to, Communication comm) throws DestinationIsOffException {
		if (to.getState().getTerminalState().equals("OFF")) {
			if (_client.getNotificationsEnabled().equals("YES")) {
				to.setCommunicationFailed();
				to.addCommFailed(comm);
			}
			throw new DestinationIsOffException(to.getKey());
		}
	}

	public void receiveCommunication(int id, Communication comm) {
		_commsReceived.put(id, comm);
	}

	public Map<Integer, Communication> getCommsSent() {
		return _commsSent;
	}

	public Map<Integer, Communication> getCommsReceived() {
		return _commsReceived;
	}

	public Collection<Communication> getCommsFailed() {
		return _commsFailed;
	}

	public void addCommFailed(Communication comm) {
		_commsFailed.add(comm);
	}

	public void clearCommsFailed() {
		_commsFailed.clear();
	}

	public Communication getComm(int id) {
		return _commsSent.get(id);
	}

	public void performPayment(int id) throws InvalidCommunicationException {
		Communication comm = getComm(id);
		assertCommunication(id);
		int price = _debtsToPay.get(id);
		_payments += price;
		_debts -= price;
		_client.performPayment(price);
		_commsPayed.put(id, comm);
	}

	public void assertCommunication(int id) throws InvalidCommunicationException {
		if (_commsPayed.get(id) != null)
			throw new InvalidCommunicationException();
		if (_debtsToPay.get(id) == null)
			throw new InvalidCommunicationException();
	}

	public long getPayments() {
		return _payments;
	}

	public long getDebts() {
		return _debts;
	}

	public void acceptCommunication(Selector<Communication> selector, CommunicationVisitor visitor)
			throws NoOngoingCommunicationException {
		if (_ongoingComm == null)
			throw new NoOngoingCommunicationException();
		if (selector.ok(_ongoingComm))
			_ongoingComm.accept(visitor, true);
	}

	public void acceptCommunication(CommunicationVisitor visitor) throws NoOngoingCommunicationException {
		acceptCommunication(new Selector<Communication>() {

		}, visitor);
	}

	public long getBalance() {
		return _payments - _debts;
	}

	public void communicated() {
		_hasCommunicated = true;
	}

	public boolean hasCommunicated() {
		return _hasCommunicated;
	}

	public boolean communicationHasFailed() {
		return _communicationFailed;
	}

	public void setCommunicationFailed() {
		_communicationFailed = true;
	}

	/**
	 * Checks if this terminal can end the current interactive communication.
	 *
	 * @return true if this terminal is busy (i.e., it has an active interactive
	 *         communication) and
	 *         it was the originator of this communication.
	 **/
	public boolean canEndCurrentCommunication() {
		if (_state.getTerminalState().equals("BUSY") && _commsSent.containsValue(_ongoingComm))
			return true;
		return false;
	}

	/**
	 * Checks if this terminal can start a new communication.
	 *
	 * @return true if this terminal is neither off neither busy, false otherwise.
	 **/
	public boolean canStartCommunication() {
		if (_state.getTerminalState().equals("BUSY") || _state.getTerminalState().equals("OFF"))
			return false;
		return true;
	}
}
