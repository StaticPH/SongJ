package com.StaticPH.MicroAud;

import com.beust.jcommander.Parameter;

import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
class CLIArguments {
	CLIArguments(){}// No instantiating!

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
//	    names = {"-f", "--file", "--files"},    //Maybe dont use the names attribute for what is effectively a required parameter...
		description = "List of all audio files to try playing",
		variableArity = true
	)
	private List<String> __audioFiles;

	private Vector<String> audioFiles = null;
	Vector<String> getAudioFiles(){
		if (audioFiles == null){
			audioFiles = new Vector<>(__audioFiles);
		}
		return audioFiles;
	}

	// ???: What happens if @SubParameter(order < 0) ??

	@Parameter(
		names = {"-d", "--traverse"},
		description = "Traverse through any directory arguments for additional audio files to play."
	)
	private boolean traverseDirectoriesEnabled = false;
	boolean isTraverseDirectoriesEnabled(){return traverseDirectoriesEnabled;}

//	@Parameter(
//		names={"--max_count"}, description = "The maximum number of files to queue up"
//	)
//	private int maxCount = 20;
//	private int getMaxCount(){return maxCount;}

	/*
	@Parameter(names = {"-modFolder", "-folder", "-mods"}, description = "Folder where mods will be downloaded")
	private String modFolder;

	@Parameter(names = {"-forceDownload"}, description = "Forces downloading instead of pulling from the cache")
	private boolean forceDownload = false;

	@Parameter(names = {"-manifest", "-manifests"}, description = "List of manifests to use for downloading/updating",
		listConverter = FileConverter.class)
	private List<File> manifests;
	*/
	@Parameter(names = {"-help"}, description = "Displays this great message", help = true)
	private boolean helpEnabled;
	boolean isHelpEnabled() { return helpEnabled;}
}