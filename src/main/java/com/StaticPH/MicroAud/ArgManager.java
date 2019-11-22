package com.StaticPH.MicroAud;

import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.Vector;

@SuppressWarnings({"WeakerAccess", "UnnecessaryReturnStatement"})
public class ArgManager {
	private CLIArguments arguments;
	private JCommander jCommander;

//	public int fileCount = 0;
//	public Vector<File> files; //TODO: Should I allow duplicate files?

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

		/*
		Traverses directories and expands the file list accordingly when enabled
		Should also discard any null or empty files; null instantiated File objects don't even seem possible though
		 */
		if (fileCount > 0) {
			//TODO: I'd love to be able to move this to occur later on, after any directory searching
			//Interestingly, "" appears to be a valid file path; it seems to be equivalent to "."?
			// Not sure if this is desirable behavior, so for now it's getting discarded
			AssortedUtils.inverseFilter(files, f -> StringUtils.isNullOrEmpty(f.getPath()));

			//FIXME: This kinda feels like bad practice to do...
			files = FileUtils.expandFileList(files, arguments.isDirectoryTraversalEnabled(), arguments.getMaxDepth());
			//???: May want to just expand the file list when its initially converted from a List to a Vector
			arguments.setAudioFiles(files);
			// can probably refactor everything above this within the if statement into a separate method, and replace files.size() below with arguments.getAudioFiles().size(); is it worth it?
			fileCount = files.size();

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
