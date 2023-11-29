package prr.app.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import prr.NetworkManager;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import prr.exceptions.MissingFileAssociationException;

/**
 * Command to save a file.
 */
class DoSaveFile extends Command<NetworkManager> {

	DoSaveFile(NetworkManager receiver) {
		super(Label.SAVE_FILE, receiver);
	}

	@Override
	protected final void execute() {
		try {
			_receiver.save();
		} catch (MissingFileAssociationException e) {
			try {
				_receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
			} catch (MissingFileAssociationException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
