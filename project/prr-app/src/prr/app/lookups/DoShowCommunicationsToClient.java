package prr.app.lookups;

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
 * Show communications to a client.
 */
class DoShowCommunicationsToClient extends Command<Network> {

	DoShowCommunicationsToClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_TO_CLIENT, receiver);
		addStringField("key", Prompt.clientKey());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			RenderCommunication renderer = new RenderCommunication();
			_receiver.acceptReceiver(renderer, _receiver.getClient(stringField("key")));
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		}

	}
}
