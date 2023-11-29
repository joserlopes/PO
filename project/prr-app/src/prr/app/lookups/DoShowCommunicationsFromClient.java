package prr.app.lookups;

import java.net.CacheRequest;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.exceptions.UnknownClientKeyException;
import prr.app.visitors.RenderCommunication;
import prr.clients.Client;
import prr.terminals.Terminal;
import prr.communications.Communication;
import prr.exceptions.ClientNotFoundException;

/**
 * Show communications from a client.
 */
class DoShowCommunicationsFromClient extends Command<Network> {

	DoShowCommunicationsFromClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_FROM_CLIENT, receiver);
		addStringField("key", Prompt.clientKey());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			RenderCommunication renderer = new RenderCommunication();
			_receiver.acceptSender(renderer, _receiver.getClient(stringField("key")));
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		}

	}
}
