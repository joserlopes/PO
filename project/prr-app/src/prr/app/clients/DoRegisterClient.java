package prr.app.clients;

import prr.Network;
import prr.app.exceptions.DuplicateClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;
import prr.exceptions.ClientExistsException;
import prr.exceptions.UnknownDataException;

/**
 * Register new client.
 */
class DoRegisterClient extends Command<Network> {

	DoRegisterClient(Network receiver) {
		super(Label.REGISTER_CLIENT, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.addStringField("name", Prompt.name());
			request.addStringField("taxId", Prompt.taxId());
			request.parse();

			_receiver.registerClient(new String[] { null,
					request.stringField("key"), request.stringField("name"),
					request.stringField("taxId")
			});
		} catch (ClientExistsException e) {
			throw new DuplicateClientKeyException(e.getKey());
		} catch (UnknownDataException e) {
			e.printStackTrace();
		}

	}

}
