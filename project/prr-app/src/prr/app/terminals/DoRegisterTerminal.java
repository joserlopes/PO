package prr.app.terminals;

import prr.Network;
import prr.app.exceptions.DuplicateTerminalKeyException;
import prr.app.exceptions.InvalidTerminalKeyException;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.UnknownDataException;
import prr.exceptions.ClientNotFoundException;
import prr.exceptions.WrongTerminalKeyException;

/**
 * Register terminal.
 */
class DoRegisterTerminal extends Command<Network> {

	DoRegisterTerminal(Network receiver) {
		super(Label.REGISTER_TERMINAL, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request1 = new Form();
			Form request2 = new Form();
			String type;

			request1.addStringField("key", Prompt.terminalKey());
			request1.parse();

			do {
				type = Form.requestString(Prompt.terminalType());
			} while (!type.equals("BASIC") && !type.equals("FANCY"));

			request2.addStringField("clientKey", Prompt.clientKey());
			request2.parse();

			_receiver.registerTerminal(new String[] {
					type, request1.stringField("key"), request2.stringField("clientKey"), null
			});
		} catch (WrongTerminalKeyException e) {
			throw new InvalidTerminalKeyException(e.getKey());
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		} catch (TerminalExistsException e) {
			throw new DuplicateTerminalKeyException(e.getKey());
		} catch (UnknownDataException e) {
			e.printStackTrace();
		}
	}
}
