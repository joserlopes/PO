package prr.app.terminals;

import java.util.Collections;

import prr.Network;
import prr.app.visitors.RenderTerminal;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all terminals.
 */
class DoShowAllTerminals extends Command<Network> {

	DoShowAllTerminals(Network receiver) {
		super(Label.SHOW_ALL_TERMINALS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderTerminal renderer = new RenderTerminal();
		_receiver.accept(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
