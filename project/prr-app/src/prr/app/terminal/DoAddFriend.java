package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.TerminalNotFoundException;
import prr.app.exceptions.UnknownTerminalKeyException;

/**
 * Add a friend.
 */
class DoAddFriend extends TerminalCommand {

	DoAddFriend(Network context, Terminal terminal) {
		super(Label.ADD_FRIEND, context, terminal);
		addStringField("terminalKey", Prompt.terminalKey());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.addFriend(stringField("terminalKey"), _network);
		} catch (TerminalNotFoundException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		}
	}
}
