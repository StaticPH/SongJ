package com.StaticPH.MicroAud;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
class CLIArguments {
	CLIArguments() {}// No instantiating!

//	public class StringListToVector implements IStringConverter<Vector<String>>{
//		@Override
//		public Vector<String> convert(String s){
//
//		}
//	}

/*
	@Parameter(names = {"-f", "--file", "--files"},
		description = "List of all audio files to try playing",
		listConverter = com.beust.jcommander.converters.FileConverter.class
	)
	private List<File> audioFiles;
	List<File> getAudioFiles(){return audioFiles;}
*/

	// ???: what behavior does the default value of arity(-1) result in
	//  Is an option treated as implicitly required if its names attribute is empty, or should it still be explicitly specified as true?


	@Parameter(
		description = "List of all audio files to try playing",
		variableArity = true,
		converter = FileConverter.class
	)
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<File> __audioFiles;    //TODO: I should really be using a converter here, but this is fine for now

	private Vector<File> audioFiles = null;

	Vector<File> getAudioFiles() {
		//TODO: May want to expand the file list here instead of during ArgManager.BaseBehavior
		//  Doing so would eliminate the need to allow other classes to modify audioFiles
		if (audioFiles == null) {
			audioFiles = new Vector<>(__audioFiles);
		}
		return audioFiles;
	}

	void setAudioFiles(Vector<File> files) { audioFiles = files;}


	// ???: What happens if @SubParameter(order < 0) ??


	@Parameter(
		names = {"-d", "--traverse"},
		description = "Traverse through any directory arguments for additional audio files to play."
	)
	private boolean traverseDirectoriesEnabled = false;

	boolean isDirectoryTraversalEnabled() {return traverseDirectoriesEnabled;}


//	@Parameter(
//		names={"--max_count"}, description = "The maximum number of files to queue up"
//	)
//	private int maxCount = 20;
//	private int getMaxCount(){return maxCount;}


	@Parameter(
		names = {"--max_depth"}, description = "Maximum number of directory levels to search."
	)
	private int maxDepth = Integer.MAX_VALUE;

	public int getMaxDepth() { return maxDepth;}

	//TODO: Validator
	//  Complain if maxDepth parameter was provided without the traversal parameter
	//  maxDepth should probably be 0 (or -1?) when traversal is not enabled.
	//  when traversal IS enabled, but maxDepth is not specified on the command line, maxDepth should EITHER be Integer.MAX_VALUE OR some sane limiting value like 5


	/*EXAMPLE
	@Parameter(names = {"-manifest", "-manifests"}, description = "List of manifests to use for downloading/updating",
		listConverter = FileConverter.class)
	private List<File> manifests;
	*/


	@Parameter(names = {"-help"}, description = "Displays this great message", help = true)
	private boolean helpEnabled;

	boolean isHelpEnabled() { return helpEnabled;}
}