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

		// Directory traversal and file list cleanup is handled during the first call to arguments.getAudioFiles();
		/*
		if (fileCount > 0) {
			// TODO: Should I filter out unsupported file types here, or elsewhere?
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
