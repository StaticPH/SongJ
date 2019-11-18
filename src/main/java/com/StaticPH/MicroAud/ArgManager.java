package com.StaticPH.MicroAud;

import com.beust.jcommander.JCommander;

import java.util.Vector;

@SuppressWarnings({"WeakerAccess", "UnnecessaryReturnStatement"})
public class ArgManager {
	private CLIArguments arguments;
	private JCommander jCommander;

	public int fileCount = 0;
	public Vector<String> files; //TODO: Should I allow duplicate files?

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
		files = arguments.getAudioFiles();  //Try to remove this
		fileCount = files.size();

		/*Discard any null or empty files */
		if (fileCount > 0) {
			//TODO: I'd love to be able to move this to occur later on, after any directory searching
			//TODO: Directory searching
			AssortedUtils.inverseFilter(files, StringUtils::isNullOrEmpty);
			if (fileCount == 0) {
				System.err.println("No supported files found within provided arguments");
			}
		}

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
