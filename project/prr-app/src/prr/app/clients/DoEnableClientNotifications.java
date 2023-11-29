package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.NotificationsAlreadyEnabledException;
import prr.exceptions.ClientNotFoundException;

/**
 * Enable client notifications.
 */
class DoEnableClientNotifications extends Command<Network> {

	DoEnableClientNotifications(Network receiver) {
		super(Label.ENABLE_CLIENT_NOTIFICATIONS, receiver);
		addStringField("key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.enableNofitications(stringField("key"));
		} catch (ClientNotFoundException e) {
			throw new UnknownClientKeyException(e.getKey());
		} catch (NotificationsAlreadyEnabledException e) {
			_display.popup(Message.clientNotificationsAlreadyEnabled());
		}
	}
}
