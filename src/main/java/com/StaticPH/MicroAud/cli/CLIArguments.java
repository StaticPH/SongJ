package com.StaticPH.MicroAud.cli;

import com.StaticPH.MicroAud.AssortedUtils;
import com.StaticPH.MicroAud.FileUtils;
import com.StaticPH.MicroAud.StringUtils;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class CLIArguments {
	CLIArguments() {}// No instantiating!

//	public class StringListToVector implements IStringConverter<Vector<String>>{
//		@Override
//		public Vector<String> convert(String s){
//
//		}
//	}


	// ???: what behavior does the default value of arity(-1) result in
	//  Is an option treated as implicitly required if its names attribute is empty, or should it still be explicitly specified as true?
	// ???: What happens if @SubParameter(order < 0) ??


	@Parameter(
		description = "List of all audio files to try playing", //consider saying something about directory parameters
		variableArity = true,
		converter = FileConverter.class
	)
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<File> __audioFiles;    //TODO: I should really be using a converter here, but this is fine for now

	private Vector<File> audioFiles = null;

	/*
	On first call, initializes the audioFiles Vector from the contents of the __audioFiles List
	If traverseDirectoriesEnabled == true, also traverses up to maxDepth directory levels, adding discovered files to the audioFiles Vector
	Returns that audioFiles Vector on subsequent calls
	 */
	public Vector<File> getAudioFiles() {
		/*
		Though I like that handling file list expansion here removes the need for a setAudioFiles method,
		it also doesn't quite feel like it belongs here. Maybe instead:
			move both ArgManager and CLIArguments into a sub-package
			create a public method here, evalFileArgs()
			create a package-private method setAudioFiles(Vector<File> files) in CLIArguments
			evalFileArgs handles the expansion, file validation, and removal of invalid files
			it then uses its package-access to setAudioFiles
		*/

		if (audioFiles == null) {
			/*
			Interestingly, "" appears to be a valid file path; it seems to be equivalent to "."?
			Not sure if this is desirable behavior, so for now it's getting discarded
			Other odd observations:
			  Using '' as a file parameter on the commandline gets interpreted as '' by the parser instead of "", but the resulting exception is not fatal
			  Java has decided not to escape spaces(even those explicitly escaped) in command line parameters enclosed in single-quotes, AND
			    throws a fatal exception upon encountering a colon in such a parameter
			 */
			AssortedUtils.inverseFilter(__audioFiles, f -> StringUtils.isNullOrBlank(f.getPath()));

			audioFiles = new Vector<>(__audioFiles);
			audioFiles = FileUtils.expandFileList(audioFiles, isDirectoryTraversalEnabled(), getMaxDepth());
		}
		return audioFiles;
	}

//	void setAudioFiles(Vector<File> files) { audioFiles = files;}


	@Parameter(
		names = {"-d", "--traverse"},
		description = "Traverse through any directory arguments for additional audio files to play."
	)
	private boolean traverseDirectoriesEnabled = false;

	public boolean isDirectoryTraversalEnabled() {return traverseDirectoriesEnabled;}


//	@Parameter(
//		names={"--max_count"}, description = "The maximum number of files to queue up"
//	)
//	private int maxCount = 20;
//	public int getMaxCount(){return maxCount;}


	@Parameter(
		names = {"--max_depth"}, description = "Maximum number of directory levels to search."
	)
	private int maxDepth = Integer.MAX_VALUE;

	public int getMaxDepth() { return maxDepth;}

	//TODO: Validator
	//  Complain if maxDepth parameter was provided without the traversal parameter
	//  maxDepth should probably be 0 (or -1?) when traversal is not enabled.
	//  when traversal IS enabled, but maxDepth is not specified on the command line, maxDepth should EITHER be Integer.MAX_VALUE OR some sane limiting value like 5
	// example: https://github.com/Nincraft/ModPackDownloader/blob/master/modpackdownloader-core/src/main/java/com/nincraft/modpackdownloader/validation/ReleaseType.java


	/*EXAMPLE
	@Parameter(names = {"-manifest", "-manifests"}, description = "List of manifests to use for downloading/updating",
		listConverter = FileConverter.class)
	private List<File> manifests;
	*/


	@Parameter(names = {"-help"}, description = "Displays this great message", help = true)
	private boolean helpEnabled;

	public boolean isHelpEnabled() { return helpEnabled;}

	/*============ HIDDEN PARAMETERS ============*/

	@Parameter(
		names = {"--list-only"}, hidden = true,
		description = "Whether to only display the final play queue, and not attempt to play anything."
	)
	private boolean disablePlayback = false;

	public boolean isPlaybackDisabled() {return disablePlayback;}

}