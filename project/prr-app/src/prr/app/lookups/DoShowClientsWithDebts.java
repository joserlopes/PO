package prr.app.lookups;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.visitors.RenderClient;

/**
 * Show clients with negative balance.
 */
class DoShowClientsWithDebts extends Command<Network> {

	DoShowClientsWithDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITH_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClient renderer = new RenderClient();
		_receiver.acceptWDebts(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
