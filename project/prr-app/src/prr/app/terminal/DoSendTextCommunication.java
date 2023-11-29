package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import prr.app.exceptions.UnknownTerminalKeyException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.DestinationIsOffException;

/**
 * Command for sending a text communication.
 */
class DoSendTextCommunication extends TerminalCommand {

	DoSendTextCommunication(Network context, Terminal terminal) {
		super(Label.SEND_TEXT_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request = new Form();
			request.addStringField("key", Prompt.terminalKey());
			request.addStringField("text", Prompt.textMessage());
			request.parse();
			_receiver.sendTextCommunication(request.stringField("key"), request.stringField("text"), _network);
		} catch (TerminalNotFoundException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		} catch (DestinationIsOffException e) {
			_display.popup(Message.destinationIsOff(e.getKey()));
		}

	}
}
