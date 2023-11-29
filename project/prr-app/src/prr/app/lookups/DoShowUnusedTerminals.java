package prr.app.lookups;

import prr.Network;
import prr.app.visitors.RenderTerminal;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show unused terminals (without communications).
 */
class DoShowUnusedTerminals extends Command<Network> {

	DoShowUnusedTerminals(Network receiver) {
		super(Label.SHOW_UNUSED_TERMINALS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderTerminal renderer = new RenderTerminal();
		_receiver.acceptNoAction(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
