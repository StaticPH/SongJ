package com.StaticPH.MicroAud;

import com.beust.jcommander.Parameter;
import java.util.List;

@SuppressWarnings("unused")
class CLIArguments {
	CLIArguments(){}// No instantiating!


	@Parameter(names = {"-f", "--file", "--files"},
		description = "List of all audio files to try playing"
	)
	private List<String> audioFiles;
	List<String> getAudioFiles(){return audioFiles;}

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