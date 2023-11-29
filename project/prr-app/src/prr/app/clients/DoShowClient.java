package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import prr.app.visitors.RenderClient;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.ClientNotFoundException;

/**
 * Show specific client: also show previous notifications.
 */
class DoShowClient extends Command<Network> {

	DoShowClient(Network receiver) {
		super(Label.SHOW_CLIENT, receiver);
		addStringField("key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			RenderClient renderer = new RenderClient();
			renderer.visitOneClient(_receiver.getClient(stringField("key")), true);
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		}
	}
}
