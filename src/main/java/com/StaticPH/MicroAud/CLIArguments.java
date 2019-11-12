package com.StaticPH.MicroAud;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.util.List;

public class CLIArguments {
	public CLIArguments(){}// No instantiating!

	@Parameter(names = {"-file", "-files"},
		description = "List of all audio files to try playing"
	)
	private List<String> audioFiles;
	public List<String> getAudioFiles(){return audioFiles;}

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
	public boolean isHelpEnabled() { return helpEnabled;}
}