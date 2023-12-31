package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.visitors.RenderCommunication;
import prr.exceptions.NoOngoingCommunicationException;

/**
 * Command for showing the ongoing communication.
 */
class DoShowOngoingCommunication extends TerminalCommand {

	DoShowOngoingCommunication(Network context, Terminal terminal) {
		super(Label.SHOW_ONGOING_COMMUNICATION, context, terminal);
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			RenderCommunication renderer = new RenderCommunication();
			_receiver.acceptCommunication(renderer);
			if (renderer.toString().length() != 0)
				_display.popup(renderer);
		} catch (NoOngoingCommunicationException e) {
			_display.popup(Message.noOngoingCommunication());
		}

	}
}
