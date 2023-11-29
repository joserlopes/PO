package prr.app.clients;

import prr.Network;
import prr.app.visitors.RenderClient;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all clients.
 */
class DoShowAllClients extends Command<Network> {

	DoShowAllClients(Network receiver) {
		super(Label.SHOW_ALL_CLIENTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClient renderer = new RenderClient();
		_receiver.accept(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
