package prr.app.terminal;

import java.net.Authenticator.RequestorType;

import prr.Network;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.UnsupportedAtOriginException;
import prr.exceptions.UnsupportedAtDestinationException;
import prr.exceptions.DestinationIsOffException;
import prr.exceptions.DestinationIsSilentException;
import prr.exceptions.DestinationIsBusyException;

/**
 * Command for starting communication.
 */
class DoStartInteractiveCommunication extends TerminalCommand {

	DoStartInteractiveCommunication(Network context, Terminal terminal) {
		super(Label.START_INTERACTIVE_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request = new Form();
			String type;

			request.addStringField("key", Prompt.terminalKey());
			request.parse();

			do {
				type = Form.requestString(Prompt.commType());
			} while (!type.equals("VIDEO") && !type.equals("VOICE"));

			_receiver.startInteractiveCommunication(request.stringField("key"), type, _network);
		} catch (TerminalNotFoundException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		} catch (UnsupportedAtOriginException e) {
			_display.popup(Message.unsupportedAtOrigin(e.getKey(), e.getType()));
		} catch (UnsupportedAtDestinationException e) {
			_display.popup(Message.unsupportedAtDestination(e.getKey(), e.getType()));
		} catch (DestinationIsOffException e) {
			_display.popup(Message.destinationIsOff(e.getKey()));
		} catch (DestinationIsSilentException e) {
			_display.popup(Message.destinationIsSilent(e.getKey()));
		} catch (DestinationIsBusyException e) {
			_display.popup(Message.destinationIsBusy(e.getKey()));
		}

	}
}
