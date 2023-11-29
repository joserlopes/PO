package prr.app.lookups;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.visitors.RenderClient;

/**
 * Show clients with positive balance.
 */
class DoShowClientsWithoutDebts extends Command<Network> {

	DoShowClientsWithoutDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITHOUT_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClient renderer = new RenderClient();
		_receiver.acceptWoDebts(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
