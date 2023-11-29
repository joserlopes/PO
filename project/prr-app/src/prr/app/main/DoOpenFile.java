package prr.app.main;

import java.io.IOError;
import java.io.IOException;

import prr.NetworkManager;
import prr.app.exceptions.FileOpenFailedException;
import prr.exceptions.UnavailableFileException;
import java.io.FileNotFoundException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

//Add more imports if needed

/**
 * Command to open a file.
 */
class DoOpenFile extends Command<NetworkManager> {

	DoOpenFile(NetworkManager receiver) {
		super(Label.OPEN_FILE, receiver);
		addStringField("openFile", Prompt.openFile());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.load(stringField("openFile"));
		} catch (UnavailableFileException e) {
			throw new FileOpenFailedException(new UnavailableFileException(stringField("openFile")));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
