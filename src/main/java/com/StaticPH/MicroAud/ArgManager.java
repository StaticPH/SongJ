package com.StaticPH.MicroAud;

import com.beust.jcommander.JCommander;

import java.util.List;

@SuppressWarnings("ALL")
public class ArgManager {
	private CLIArguments arguments;
	private JCommander jCommander;

	public int fileCount = 0;
	public List<String> files; //TODO: Should I allow duplicate files?

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

	private void baseBehvaior(CLIArguments arguments) {
		files = arguments.getAudioFiles();  //Try to remove this
		fileCount = files.size();

		/*Discard any null or empty files */
		if (fileCount > 0) {
			AssortedUtils.inverseFilter(files, StringUtils::isNullOrEmpty);
		}

		/* If no files were given, display the program help and ignore further arguments.*/
		if (fileCount == 0) {
			getJCommander().usage();
			return;
		}
	}

	// Only ever run this AFTER instantiating ArgManager!!
	public void init() {
		baseBehvaior(arguments); //Could probably just have defaultArgs access the value of arguments itself...
	}
}
