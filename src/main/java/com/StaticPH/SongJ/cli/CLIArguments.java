package com.StaticPH.SongJ.cli;

import com.StaticPH.SongJ.AssortedUtils;
import com.StaticPH.SongJ.FileUtils;
import com.StaticPH.SongJ.StringUtils;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal", "RedundantSuppression"})
public class CLIArguments {
	CLIArguments() {}// No instantiating from outside the package!

//	public class StringListToVector implements IStringConverter<Vector<String>>{
//		@Override
//		public Vector<String> convert(String s){
//
//		}
//	}

	@Parameter(
		description = "File...",
		variableArity = true,
		order = 0//,
	)
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<String> __audioFiles;    //TODO: I should really be using a listConverter here, but this is fine for now
	static final String realAudioFilesParamDescription =
		"FILE is a space-separated list of one or more audio files to try playing, " +
		"and can also include directories through which to traverse for audio files.";
	private Vector<File> audioFiles = null;

	/*
	On first call, initializes the audioFiles Vector from the contents of the __audioFiles List
	If traverseDirectoriesEnabled == true, also traverses up to maxDepth directory levels, adding discovered files to the audioFiles Vector
	Returns that audioFiles Vector on subsequent calls.
	If there are no file parameters, or no files remain after expansion and filtering, returns null.
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

		if (this.__audioFiles == null) {return null;}
		else if (this.audioFiles == null) {
			/*
			Interestingly, "" appears to be a valid file path; it seems to be equivalent to "."?
			Not sure if this is desirable behavior, so for now it's getting discarded
			Other odd observations:
			  Using '' as a file parameter on the commandline gets interpreted as '' by the parser instead of "", but the resulting exception is not fatal
			  Java has decided not to escape spaces(even those explicitly escaped) in command line parameters enclosed in single-quotes, AND
			    throws a fatal exception upon encountering a colon in such a parameter
			 */
			this.audioFiles = this.__audioFiles.stream().map(File::new).collect(Collectors.toCollection(Vector::new));
			AssortedUtils.inverseFilter(this.audioFiles, f -> StringUtils.isNullOrBlank(f.getPath()));

			this.audioFiles = FileUtils.expandFileList(this.audioFiles, this.traverseDirectoriesEnabled, this.maxDepth);
		}
		return this.audioFiles;
	}

//	void setAudioFiles(Vector<File> files) { audioFiles = files;}


	@Parameter(
		names = {"-d", "--traverse"},
		description = "Traverse through any directory arguments for additional audio files to play.",
		order = 1
	)
	private boolean traverseDirectoriesEnabled = false;

	public boolean isDirectoryTraversalEnabled() {return this.traverseDirectoriesEnabled;}


//	@Parameter(
//		names={"--max_count"}, description = "The maximum number of files to queue up"
//	)
//	private int maxCount = 20;
//	public int getMaxCount(){return maxCount;}


	@Parameter(
		names = {"--max_depth"}, description = "Maximum number of directory levels to search.", order = 2
	)
	private int maxDepth = Integer.MAX_VALUE;

	public int getMaxDepth() { return this.maxDepth;}

	//TODO: Validator
	//  Complain if maxDepth parameter was provided without the traversal parameter
	//  maxDepth should probably be 0 (or -1?) when traversal is not enabled.
	//  when traversal IS enabled, but maxDepth is not specified on the command line, maxDepth should EITHER be Integer.MAX_VALUE OR some sane limiting value like 5
	// example: https://github.com/Nincraft/ModPackDownloader/blob/master/modpackdownloader-core/src/main/java/com/nincraft/modpackdownloader/validation/ReleaseType.java


	@Parameter(names="--color", description = "Enables colored output")
	private boolean colorOutput = false;

	public boolean useColor(){return this.colorOutput;}

	/*EXAMPLE
	@Parameter(names = {"-manifest", "-manifests"}, description = "List of manifests to use for downloading/updating",
		listConverter = FileConverter.class)
	private List<File> manifests;
	*/

	@Parameter(names = {"-h", "--help"}, description = "Displays this help message", help = true)
	private boolean helpEnabled;

	public boolean isHelpEnabled() { return this.helpEnabled;}

	/*============ HIDDEN PARAMETERS ============*/

	@Parameter(
		names = {"--list_only"}, hidden = true,
		description = "Whether to only display the final play queue, and not attempt to play anything."
	)
	private boolean disablePlayback = false;

	public boolean isPlaybackDisabled() {return this.disablePlayback;}

}