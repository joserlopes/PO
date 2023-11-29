package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.ClientNotFoundException;
import prr.clients.Client;

/**
 * Show the payments and debts of a client.
 */
class DoShowClientPaymentsAndDebts extends Command<Network> {

	DoShowClientPaymentsAndDebts(Network receiver) {
		super(Label.SHOW_CLIENT_BALANCE, receiver);
		addStringField("key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Client client = _receiver.getClient(stringField("key"));
			_display.popup(Message.clientPaymentsAndDebts(stringField("key"), client.getPayments(), client.getDebts()));
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		}
	}
}
