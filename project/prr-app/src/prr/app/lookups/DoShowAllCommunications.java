package prr.app.lookups;

import java.lang.ProcessBuilder.Redirect;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.visitors.RenderCommunication;

/**
 * Command for showing all communications.
 */
class DoShowAllCommunications extends Command<Network> {

	DoShowAllCommunications(Network receiver) {
		super(Label.SHOW_ALL_COMMUNICATIONS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderCommunication renderer = new RenderCommunication();
		_receiver.accept(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
