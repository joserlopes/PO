package prr;

import java.io.Serializable;
import java.security.UnrecoverableEntryException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

import prr.exceptions.UnrecognizedEntryException;
import prr.clients.Client;
import prr.clients.ClientComparator;
import prr.clients.ClientDebtsComparator;
import prr.terminals.BasicTerminal;
import prr.terminals.FancyTerminal;
import prr.terminals.Terminal;
import prr.communications.Communication;
import prr.communications.CommunicationComparator;
import prr.exceptions.ClientExistsException;
import prr.exceptions.ImportFileException;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.UnknownDataException;
import prr.exceptions.ClientNotFoundException;
import prr.exceptions.WrongTerminalKeyException;
import prr.exceptions.NotificationsAlreadyEnabledException;
import prr.exceptions.NotificationsAlreadyDisabledException;
import prr.visits.ClientVisitor;
import prr.visits.TerminalVisitor;
import prr.visits.CommunicationVisitor;
import prr.visits.Selector;

/**
 * Class Store implements a store.
 */
public class Network implements Serializable {

	/** Serial number for serialization. */
	private static final long serialVersionUID = 202208091753L;

	/* Clients */
	private Map<String, Client> _clients = new TreeMap<>(new ClientComparator());

	/* Terminals */
	private Map<String, Terminal> _terminals = new TreeMap<>();

	private Map<Integer, Communication> _comms = new TreeMap<>();

	/* Network object has been changed */
	private boolean _changed = false;

	private int _nComms = 1;

	/**
	 * 
	 * Network constructor
	 */
	public Network() {
		setChanged(false);
	}

	/**
	 * 
	 * Set changed
	 */
	public void changed() {
		setChanged(true);
	}

	/**
	 * 
	 * @return changed
	 */
	public boolean hasChanged() {
		return _changed;
	}

	/**
	 * 
	 * @param changed
	 */
	public void setChanged(boolean changed) {
		_changed = changed;
	}

	/**
	 * Read text input file and create corresponding domain entities.
	 * 
	 * @param filename name of the text input file
	 * @throws UnrecognizedEntryException if some entry is not correct
	 * @throws IOException                if there is an IO erro while processing
	 *                                    the text file
	 * @throws ImportFileException        if there was an error importing the file
	 */
	void importFile(String filename) throws UnrecognizedEntryException, IOException, ImportFileException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\\|");
				try {
					registerEntry(fields);
				} catch (UnknownDataException | ClientExistsException | TerminalExistsException
						| ClientNotFoundException | WrongTerminalKeyException | TerminalNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			throw new ImportFileException(filename);
		}
	}

	/**
	 * 
	 * @param fields
	 * @throws UnknownDataException
	 * @throws ClientExistsException
	 * @throws TerminalExistsException
	 * @throws ClientNotFoundException
	 */
	public void registerEntry(String... fields)
			throws UnknownDataException, ClientExistsException, TerminalExistsException, ClientNotFoundException,
			WrongTerminalKeyException, TerminalNotFoundException {
		switch (fields[0]) {
			case "CLIENT" -> registerClient(fields);
			case "BASIC", "FANCY" -> registerTerminal(fields);
			case "FRIENDS" -> addMultipleTerminalFriends(fields[1], fields[2].split(","));
			default -> throw new UnknownDataException(fields[0]);
		}
	}

	/**
	 * 1 - key; 2 - name; 3 - taxId
	 * 
	 * @param fields
	 * @throws ClientExistsException
	 * @throws UnknownDataException
	 */
	public void registerClient(String[] fields) throws ClientExistsException, UnknownDataException {
		Client client = new Client(fields[1], fields[2], Integer.parseInt(fields[3]));
		addClient(fields[1], client);
		changed();
	}

	/**
	 * 
	 * @param key
	 * @param client
	 * @throws ClientExistsException
	 */
	public void addClient(String key, Client client) throws ClientExistsException {
		assertNewClient(key);
		_clients.put(key, client);
		changed();
	}

	/**
	 * 1 - key; 3 - state
	 * 
	 * @param fields
	 * @throws TerminalExistsException
	 * @throws UnknownDataException
	 * @throws ClientNotFoundException
	 * @throws WrongTerminalKeyException
	 */
	public void registerTerminal(String... fields)
			throws TerminalExistsException, UnknownDataException, ClientNotFoundException, WrongTerminalKeyException {
		Terminal terminal = switch (fields[0]) {
			case "BASIC" -> new BasicTerminal(fields[1], fields[3], fields[0]);
			case "FANCY" -> new FancyTerminal(fields[1], fields[3], fields[0]);
			default -> throw new UnknownDataException(fields[0]);
		};

		addTerminal(fields[1], fields[2], terminal);
		changed();
	}

	/**
	 * 
	 * @param key
	 * @param clientKey
	 * @param terminal
	 * @throws TerminalExistsException
	 * @throws ClientNotFoundException
	 * @throws WrongTerminalKeyException
	 */
	public void addTerminal(String key, String clientKey, Terminal terminal)
			throws TerminalExistsException, ClientNotFoundException,
			WrongTerminalKeyException {
		assertNewTerminal(key);
		assertNewTerminalKey(key);
		Client owner = getClient(clientKey);
		owner.addTerminal(key, terminal);
		terminal.setClient(owner);
		_terminals.put(key, terminal);
		changed();
	}

	/**
	 * Visits the selected client.
	 * 
	 * @param selector
	 * @param visitor
	 */
	public void accept(Selector<Client> selector, ClientVisitor visitor) {
		int clientCount = 0;
		int numberOfClients = _clients.keySet().size();
		for (Client client : _clients.values()) {
			++clientCount;
			if (selector.ok(client)) {
				client.accept(visitor, clientCount == numberOfClients);
			}
		}

	}

	/**
	 * Visit the selected client.
	 * 
	 * @param visitor
	 */
	public void accept(ClientVisitor visitor) {
		accept(new Selector<Client>() {

		}, visitor);
	}

	public void acceptWDebts(Selector<Client> selector, ClientVisitor visitor) {
		int clientCount = 0;
		int numberOfClients = 0;
		for (Client client : _clients.values()) {
			if (client.getDebts() > 0)
				numberOfClients++;
		}

		Client[] clients = _clients.values().toArray(new Client[_clients.size()]);

		Arrays.sort(clients, new ClientDebtsComparator());

		for (Client client : clients) {
			if (client.getDebts() > 0) {
				++clientCount;
				if (selector.ok(client))
					client.accept(visitor, clientCount == numberOfClients);
			}
		}
	}

	public void acceptWDebts(ClientVisitor visitor) {
		acceptWDebts(new Selector<Client>() {

		}, visitor);
	}

	public void acceptWoDebts(Selector<Client> selector, ClientVisitor visitor) {
		int clientCount = 0;
		int numberOfClients = 0;
		for (Client client : _clients.values()) {
			if (client.getDebts() == 0)
				numberOfClients++;
		}
		for (Client client : _clients.values()) {
			if (client.getDebts() == 0) {
				++clientCount;
				if (selector.ok(client))
					client.accept(visitor, clientCount == numberOfClients);
			}
		}
	}

	public void acceptWoDebts(ClientVisitor visitor) {
		acceptWoDebts(new Selector<Client>() {

		}, visitor);
	}

	/**
	 * 
	 * @param key
	 * @throws ClientExistsException
	 */
	public void assertNewClient(String key) throws ClientExistsException {
		if (_clients.containsKey(key))
			throw new ClientExistsException(key);
	}

	/**
	 * 
	 * @param key
	 * @throws TerminalExistsException
	 */
	public void assertNewTerminal(String key) throws TerminalExistsException {
		if (_terminals.containsKey(key))
			throw new TerminalExistsException(key);
	}

	/**
	 * 
	 * @param key
	 * @throws WrongTerminalKeyException
	 */
	public void assertNewTerminalKey(String key) throws WrongTerminalKeyException {
		if (key.length() != 6 || !TerminalKeyCharacters(key))
			throw new WrongTerminalKeyException(key);
	}

	/**
	 * 
	 * @param key
	 * @return true if terminal key has only numeric characters, false otherwise
	 */
	public boolean TerminalKeyCharacters(String TerminalKey) {
		for (int i = 0; i < TerminalKey.length(); i++) {
			if (!Character.isDigit(TerminalKey.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * Visit the selected terminal
	 * 
	 * @param selector
	 * @param visitor
	 */
	public void accept(Selector<Terminal> selector, TerminalVisitor visitor) {
		int terminalCount = 0;
		int numberOfTerminals = _terminals.keySet().size();
		for (Terminal terminal : _terminals.values()) {
			++terminalCount;
			if (selector.ok(terminal))
				terminal.accept(visitor, terminalCount == numberOfTerminals);
		}
	}

	/**
	 * Visit the selected terminal
	 * 
	 * @param visitor
	 */
	public void accept(TerminalVisitor visitor) {
		accept(new Selector<Terminal>() {

		}, visitor);
	}

	public void acceptPositiveBalance(Selector<Terminal> selector, TerminalVisitor visitor) {
		int terminalCount = 0;
		int numberOfTerminals = 0;
		for (Terminal terminal : _terminals.values()) {
			if (terminal.getBalance() > 0)
				numberOfTerminals++;
		}
		for (Terminal terminal : _terminals.values()) {
			if (terminal.getBalance() > 0) {
				++terminalCount;
				if (selector.ok(terminal))
					terminal.accept(visitor, terminalCount == numberOfTerminals);
			}
		}
	}

	public void acceptPositiveBalance(TerminalVisitor visitor) {
		acceptPositiveBalance(new Selector<Terminal>() {

		}, visitor);
	}

	public void acceptNoAction(Selector<Terminal> selector, TerminalVisitor visitor) {
		int terminalCount = 0;
		int numberOfTerminals = 0;
		for (Terminal terminal : _terminals.values()) {
			if (!terminal.hasCommunicated())
				numberOfTerminals++;
		}
		for (Terminal terminal : _terminals.values()) {
			if (!terminal.hasCommunicated()) {
				++terminalCount;
				if (selector.ok(terminal))
					terminal.accept(visitor, terminalCount == numberOfTerminals);
			}
		}
	}

	public void acceptNoAction(TerminalVisitor visitor) {
		acceptNoAction(new Selector<Terminal>() {

		}, visitor);
	}

	/**
	 * Get the terminal with the given key
	 * 
	 * @param key
	 * @return
	 * @throws TerminalNotFoundException
	 */
	public Terminal getTerminal(String key) throws TerminalNotFoundException {
		return fetchTerminal(key);
	}

	/**
	 * 
	 * @param key
	 * @return the terminal identified by the key
	 * @throws TerminalNotFoundException
	 */
	private Terminal fetchTerminal(String key) throws TerminalNotFoundException {
		if (!_terminals.containsKey(key))
			throw new TerminalNotFoundException(key);
		return _terminals.get(key);
	}

	/**
	 * Get the client with the given key
	 * 
	 * @param key
	 * @return
	 * @throws ClientNotFoundException
	 */
	public Client getClient(String key) throws ClientNotFoundException {
		return fetchClient(key);
	}

	/**
	 * 
	 * @param key
	 * @return the client identified by the key
	 * @throws ClientNotFoundException
	 */
	private Client fetchClient(String key) throws ClientNotFoundException {
		if (!_clients.containsKey(key))
			throw new ClientNotFoundException(key);
		return _clients.get(key);
	}

	public void addMultipleTerminalFriends(String key, String... friends)
			throws TerminalNotFoundException {
		Terminal terminalToAdd = getTerminal(key);
		for (int i = 0; i < friends.length; i++)
			terminalToAdd.addFriend(friends[i], this);
	}

	public void accept(Selector<Communication> selector, CommunicationVisitor visitor) {
		int commCount = 0;
		int numberOfComms = _comms.keySet().size();

		for (Communication comm : _comms.values()) {
			++commCount;
			if (selector.ok(comm))
				comm.accept(visitor, commCount == numberOfComms);
		}
	}

	public void accept(CommunicationVisitor visitor) {
		accept(new Selector<Communication>() {
		}, visitor);
	}

	public void acceptSender(Selector<Communication> selector, CommunicationVisitor visitor, Client client) {
		int commCount = 0;
		Collection<Communication> commsSent = client.getCommsSent().values();
		int numberOfComms = commsSent.size();

		for (Communication comm : commsSent) {
			++commCount;
			if (selector.ok(comm))
				comm.accept(visitor, commCount == numberOfComms);
		}
	}

	public void acceptReceiver(Selector<Communication> selector, CommunicationVisitor visitor, Client client) {
		int commCount = 0;
		Collection<Communication> commsReceived = client.getCommsReceived().values();
		int numberOfComms = commsReceived.size();
		for (Communication comm : commsReceived) {
			++commCount;
			if (selector.ok(comm))
				comm.accept(visitor, commCount == numberOfComms);
		}
	}

	public void acceptSender(CommunicationVisitor visitor, Client client) {

		acceptSender(new Selector<Communication>() {
		}, visitor, client);
	}

	public void acceptReceiver(CommunicationVisitor visitor, Client client) {

		acceptReceiver(new Selector<Communication>() {
		}, visitor, client);
	}

	public void newComm(int id, Communication comm) {
		_comms.put(id, comm);
		communicationHappened();
	}

	public void communicationHappened() {
		_nComms++;
	}

	public int getNComms() {
		return _nComms;
	}

	public long getGlobalPlayments() {
		long payments = 0;
		for (Client client : _clients.values())
			payments += client.getPayments();
		return payments;
	}

	public long getGlobalDebts() {
		long debts = 0;
		for (Client client : _clients.values())
			debts += client.getDebts();
		return debts;
	}

	public void enableNofitications(String key) throws ClientNotFoundException, NotificationsAlreadyEnabledException {
		Client client = getClient(key);
		client.enableNofitications();
	}

	public void disableNofitications(String key) throws ClientNotFoundException, NotificationsAlreadyDisabledException {
		Client client = getClient(key);
		client.disableNofitications();
	}
}
