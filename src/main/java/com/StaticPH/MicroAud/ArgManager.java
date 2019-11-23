package com.StaticPH.MicroAud;

import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.Vector;

@SuppressWarnings({"WeakerAccess", "UnnecessaryReturnStatement"})
public class ArgManager {
	private CLIArguments arguments;
	private JCommander jCommander;

	public CLIArguments getArguments() { return arguments;}

	public JCommander getJCommander() { return jCommander;}

	public ArgManager(String[] args) {
		arguments = new CLIArguments();
		jCommander = initArgs(args);
	}

	private JCommander initArgs(final String[] args) {
		// ???: Is there really any reason for this, if initArgs is only ever called after instantiating ArgManager.arguments?
		arguments = new CLIArguments();
		return new JCommander(arguments, null, args);
	}

	private void baseBehavior(CLIArguments arguments) {
		Vector<File> files = arguments.getAudioFiles();  //Try to remove this
		int fileCount = files.size();

		// Directory traversal is handled during the first call to arguments.getAudioFiles();
		// TODO: create and call some function that will check for the existence of every file in getAudioFiles, and remove those that dont exist
		//  May want that to occur in CLIArguments too, although having directory traversal occur there already feels rather out of place

		/*alternatively:
		move both ArgManager and CLIArguments into a sub-package
		create a public method here, evalFileArgs()
		create a package-private method setAudioFiles(Vector<File> files) in CLIArguments
		evalFileArgs handles the expansion, file validation, and removal of invalid files
		it then uses its package-access to setAudioFiles

		*/
		/*
		if (fileCount > 0) {
			// Should I filter out unsupported file types here, or elsewhere?
			fileCount = files.size();

			if (fileCount == 0) {
				System.err.println("No supported files found within provided arguments");
			}
		}
		*/
		/* If no files were given, display the program help and ignore further arguments.*/
		if (fileCount == 0) {
			getJCommander().usage();
			return;
		}
	}

	// Only ever run this AFTER instantiating ArgManager!!
	public void init() {
		baseBehavior(arguments); //Could probably just have defaultArgs access the value of arguments itself...
	}
}
