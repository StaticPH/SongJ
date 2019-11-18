package com.StaticPH.MicroAud;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Vector;


@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class MicroAud {
	/* ======= Class (Static) Variables ======= */

	// Question: Do I want to keep the interface, or switch to an abstract class, or even a non-abstract,
	//           so I can use methods in a static context without a static variable of that class's type?
	// ???: is it worth declaring them as final?
//	private static IFileUtils Util;     //NOTE: I WANT TO GET RID OF THIS!!
	private static final AssortedUtils au = new AssortedUtils();        //NOTE: I WANT TO GET RID OF THIS!!
	private static final StringUtils sUtil = new StringUtils();        //NOTE: I WANT TO GET RID OF THIS!!


	//======= Instance (Non-Static) Variables =======

	//======= Class Constructors =======
	public MicroAud() {}

	//======= Methods =======

	// Consider extracting this to AssortedUtils and simply taking a String[] parameter containing
	// any files specified from the command line; said argument would be null in the absence of arguments
	// Even just using the parameter mechanic would eliminate the need for this (and hasFileArgs) to be static,
	// and possibly the existence of hasFileArgs entirely
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

//	public static File[] getFilesToPlay(List<String> files) {
//		File[] files = au.getFileFromPath()
//		//		File[] getFiles-> {for(String s: arguments.getAudioFiles()){au.getFileFromPath(s);}};
//	}

	@SuppressWarnings("Convert2Diamond")
	public static Vector<File> getFilesToPlay(CLIArguments arguments) {
		Vector<File> filesToPlay = new Vector<File>();
		for (String s : arguments.getAudioFiles()) {
			File f = FileUtils.getFileFromPath(s);
			if (f != null) {filesToPlay.add(f);}
		}
		return filesToPlay;
	}

	/*
	static Vector<File> getFilesToPlay(CLIArguments arguments){
		Vector<File> vf= new Vector<>();
		Runnable runnable = () -> {
			arguments.getAudioFiles().forEach((b) -> vf.add(au.getFileFromPath(b)));
		};
		return vf;
	}*/

	/*
	static Vector<File> getFilesToPlay(CLIArguments arguments) {
		Vector<File> vf = new Vector<>();
		Runnable runnable = () -> arguments.getAudioFiles().forEach(
			(b) -> {
				File f = au.getFileFromPath(b);
				if (f != null) { vf.add(f);}
			}
		);
		return vf;
	}
	*/


	/**
	 * Play <tt>.wav</tt>, <tt>.au</tt>, or <tt>.aiff</tt> files, and anything else included in
	 * {@link AudioSystem#getAudioFileTypes()}
	 * <p></p>
	 * Should also support the MIDI based song file formats:
	 *      SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 * @param file      A <tt>File</tt> of any supported file type
	 *
	 * @see AudioSystem#getAudioFileTypes()
	 */
	public static void playBasic(File file) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			try (Clip clip = AudioSystem.getClip()) {
				clip.open(ais);
				clip.start();
				AssortedUtils.getLogger().info("Now playing from: \"" + file.getName() + "\"");
				Thread.sleep(clip.getMicrosecondLength() / 1000);
			}
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Play audio from an <tt>InputStream</tt> opened to <tt>.wav</tt>, <tt>.au</tt>, or <tt>.aiff</tt>
	 * files, or any other file type supported
	 * according to {@link AudioSystem#getAudioFileTypes()}
	 * <p></p>
	 * Should also support an <tt>InputStream</tt> opened to MIDI based song file formats:
	 *      SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 *
	 * @param audIn     An <tt>InputStream</tt> opened to a file supported by {@link AudioSystem}
	 * @param name      Optional: name of the file audio is being played from
	 *
	 * @see #playBasic(File)
	 * @see AudioSystem#getAudioFileTypes()
	 */
	public static void playClipFromStream(AudioInputStream audIn, String name) {
		try (Clip clip = AudioSystem.getClip()) {
			clip.open(audIn);
			clip.start();

			if (!StringUtils.isNullOrEmpty(name)) {
				AssortedUtils.getLogger().info("Now playing from: \"" + name + "\"");
			}
			else{ AssortedUtils.getLogger().info("Now playing from unknown audio file.");}

			// Main thread still dies before audio starts
//			while (true){
//				if (!clip.isRunning()){
//					break;
//				}
//			}

			// Keeps main thread alive while audio plays
			try { Thread.sleep(clip.getMicrosecondLength() / 1000);}
			catch (InterruptedException e) { e.printStackTrace();}
		}
		catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see #playClipFromStream(AudioInputStream, String) playClipFromStream(AudioInputStream, String=null)
	 */
	public static void playClipFromStream(AudioInputStream audIn){playClipFromStream(audIn, null);}

	public static void doAud(File file) {
		try {
			AudioInputStream audIn = AudioSystem.getAudioInputStream(file);
			playClipFromStream(audIn, file.getName());
		}
		catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

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

		Vector<File> filesToPlay = new Vector<>();
		for (String s : arguments.getAudioFiles()) {
			File f = FileUtils.getFileFromPath(s);
			if (f != null) {filesToPlay.add(f);}
		}
		filesToPlay.forEach(MicroAud::doAud);

	}
}

/*
OUTSIDE CALLS TO NON-STATIC METHODS MUST** BE CALLED USING AN INSTANCE OF THEIR CLASS
ex:
	class Foobar{
		public void foo(){
		  //do stuff
		}
	}
	class Main{
		public static void main(String[] args){
			Foobar bar = new Foobar();
			bar.foo();
		}
	}


** THE EXCEPTION TO THIS IS WHEN EXTENDING AN ABSTRACT CLASS;
	IN ORDER TO USE A METHOD IMPLEMENTATION DESCRIBED WITHIN THE ABSTRACT CLASS, THAT METHOD MUST BE CALLED USING THE
	NAME OF THE ABSTRACT CLASS.
	IF TRYING TO USE AN IMPLEMENTATION FROM AN OVERRIDE, THIS CASE DOES NOT APPLY.
ex:
	abstract class Foobar{
		public void foo(){
		  //do stuff, has actual implementation
		}
	}
	class Main extends Foobar{
		public static void main(String[] args){
			Foobar.foo();
		}
	}

*/
