package com.StaticPH.MicroAud;

import com.StaticPH.MicroAud.audioPlayer.AbstractAudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.BasicAudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.Mp3AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;


@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class MicroAud {
	/* ======= Class (Static) Variables ======= */

	// Question: Do I want to keep the interface, or switch to an abstract class, or even a non-abstract,
	//           so I can use methods in a static context without a static variable of that class's type?
	// ???: is it worth declaring them as final?
	private static final AssortedUtils au = new AssortedUtils();        //NOTE: I WANT TO GET RID OF THIS!!


	//======= Instance (Non-Static) Variables =======

	//======= Class Constructors =======
	public MicroAud() {}

	//======= Methods =======

	@SuppressWarnings("ConstantConditions")
	@Deprecated
	public static File getFile() {
		File file;
		System.out.println("Possible fs roots: " + Arrays.toString(File.listRoots()));
		// this stupid thing CLEARLY knows I have both a C and a D drive, so why the fuck wont it try interpreting '/c/' as 'C:\' and '/d/' as 'D:\'
		/*
		TODO: make it so that unix-style paths properly resolve when prefixed with a drive letter
			//https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/FilenameUtils.html#getPrefix(java.lang.String)
			//investigate java.nio.file.Path ; and java.nio.file.Paths.get ; java.nio.file.FileSystem.getRootDirectories()
			// file.toPath().*; particularly note toRealPath, toAbsolutePath, and getRoot
			Maybe like so:
			1. roots = File.listRoots()
			2. if (file.getPath().startsWith('/')){
				3. seg1= <somehow get first segment of path>; maybe use Path.getName(0)
				4. for root in roots{
				//need to see how this reacts to inputs containing '\ ', and if i maybe need to use replaceAll to replace multiple chars
					5. if (seg1.equalsIgnoreCase(root.replace(':','').replace('\\','')){
						//this is *probably* supposed to be a drive letter; will have to be wary of behaviour on systems where root is /
						6. modify file.path, replacing its first segment with the windows-style drive letter (value of root)
						 .....??????
			n. else{
				n+1. //assume the path's parent is whatever the default filesystem is, and just use f.getCanonicalPath()
			}
		*/

		System.out.print("File to read from: ");
		//TODO: work this function so that it handles multiple file arguments from the command line
		// If possible, do so without creating a static variable containing either the number of
		//      file arguments or their values

		//TODO: support adding all media files in directory
//			try( file = au.getFileFromPath(???))
		System.out.println("File arguments on the command line have not yet been implemented.");
		file = null;//Temporary: Just until command line arguments are implemented

//		if (!file.isDirectory()) {
//			System.out.println("Reading from: " + file.getAbsolutePath());
//		}
//		else { System.out.printf("Error: '%s' is a directory.\n", file.getAbsolutePath());}
		return file;
	}

	//	@SuppressWarnings("CodeBlock2Expr")
	public static void printPlayQueue(Vector<File> filesToPlay) {
		//Could technically use a Collection<File> instead
		System.out.println("Queue" + "(length prior to type filtering: " + filesToPlay.size() + "): \n");
		filesToPlay.stream()
//		           .filter(file -> {
//			           return file.getPath().endsWith(".wav");
//		           }) //TODO: REMOVE ME!! I LIMIT THE OUTPUT TO WAV FILES
                   .map(file -> "      " + file.getPath())
                   .forEach(System.out::println);
	}

	public static void doAud(File file) {
		try {
			AbstractAudioPlayer a;
			//FIXME: TEMPORARY! REPLACE WITH FACTORY ASAP!
			if (file.getPath().endsWith(".mp3")) {
				a = new Mp3AudioPlayer();
			}
			else {
				a = new BasicAudioPlayer();
			}
			System.out.println("ATTN: NOW \"" + file.getPath() + "\" IS SUPPOSED TO BE PLAYING");
			a.playFile(file);
		}
		catch (UnsupportedAudioFileException e) { e.printStackTrace();}
	}

	public static void doAud(Path path) { doAud(path.toFile());}

/*
	//https://stackoverflow.com/a/10645913
	public static File getRc(String name) throws URISyntaxException {
		MicroAud mAud = new MicroAud();
		return new File(MicroAud.class.getResource(name).toURI());
//		return mAud.getClass().getResource(name).getFile();
//		mAud.getClass().getResourceAsStream(name);
	}
*/

	/*public static void selectPlayer(String filepath) {
		int extSep = filepath.lastIndexOf('.');
		if (extSep == -1) { extSep = 0;}
		String ext = filepath.substring(extSep).toLowerCase();
		switch (ext) {
			case "wav":
			case "au":
			case "aiff":
				return MicroAud::playBasic;// how do? i basically want function pointers...at least i think thats how i want this to work...
			case "mp3":
				return;
			case "ogg":
				return;
			default:
				System.out.println("Unsupported media type: " + ext);
		}
	}*/

	public static void main(String[] args) {
//		AssortedUtils.logArgs(args);

		ArgManager argManager = new ArgManager(args);
		CLIArguments arguments = argManager.getArguments();

		if (arguments.isHelpEnabled()) {
			argManager.getJCommander().usage();
			return;
		}

		argManager.init();

		//TODO: see if its feasible to provide a means of converting a wav file to a (roughly) equivalent midi file

		Vector<File> filesToPlay = new Vector<>(arguments.getAudioFiles());

		if (arguments.isPlaybackDisabled()) {AssortedUtils.getLogger().info("Playback Disabled");}

		printPlayQueue(filesToPlay);
		if (!arguments.isPlaybackDisabled()) {
			filesToPlay.forEach(MicroAud::doAud);
		}
	}
}