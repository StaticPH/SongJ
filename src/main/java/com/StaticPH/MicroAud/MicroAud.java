package com.StaticPH.MicroAud;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class MicroAud {
	/* ======= Class (Static) Variables ======= */

	// Question: Do I want to keep the interface, or switch to an abstract class, or even a non-abstract,
	//           so I can use methods in a static context without a static variable of that class's type?
	// ???: is it worth declaring them as final?
//	private static IFileUtils Util;     //NOTE: I WANT TO GET RID OF THIS!!
	private static final AssortedUtils au = new AssortedUtils();        //NOTE: I WANT TO GET RID OF THIS!!
	private static final StringUtils sUtil = new StringUtils();        //NOTE: I WANT TO GET RID OF THIS!!

	private static boolean hasFileArgs = false;

	/* ======= Instance (Non-Static) Variables ======= */

	/* ======= Class Constructors ======= */
	public MicroAud() {}

	/* ======= Methods ======= */

	// Consider extracting this to AssortedUtils and simply taking a String[] parameter containing
	// any files specified from the command line; said argument would be null in the absence of arguments
	// Even just using the parameter mechanic would eliminate the need for this (and hasFileArgs) to be static,
	// and possibly the existence of hasFileArgs entirely
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
		// NOTE: I'd LIKE to be able to call `IFileUtils.getFileFromScanner() instead,
		// and have the compiler be smart enough to find the desired implementation on its own`
		if (!hasFileArgs) {
			if ((file = au.getFileFromScanner()) != null) {
				if (!file.isDirectory()) {
					System.out.println("Reading from: " + file.getAbsolutePath());
				}
				else { System.out.printf("Error: '%s' is a directory.\n", file.getAbsolutePath());}
			}
		}
		else {
			//TODO: work this function so that it handles multiple file arguments from the command line
			// If possible, do so without creating a static variable containing either the number of
			//      file arguments or their values

//			try( file = au.getFileFromPath(???))
//			?????
			System.out.println("File arguments on the command line have not yet been implemented.");
			file = null;//Temporary: Just until command line arguments are implemented
		}
		return file;
	}

	// This could be made non-static if a local MicroAud variable was instantiated in main()
	public static void parseArgs(String[] args) {

		// if the program received command line arguments for files, set hasFileargs = true
		hasFileArgs = false; // Temporary: for now, hardcode to false
	}//TODO?


	/*Specifically useful for Wav files, and a handful of other types that likely wont be used anyways*/
	public static void playWav(File file) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
			Thread.sleep(clip.getMicrosecondLength() / 1000);
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void playClip(AudioInputStream audIn) {
		try (Clip clip = AudioSystem.getClip()) {
			clip.open(audIn);
			clip.start();
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

	public static void doAud(File file) {
		try {
			AudioInputStream audIn = AudioSystem.getAudioInputStream(file);
			playClip(audIn);
		}
		catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		AssortedUtils.logArgs(args);

		// TODO: prioritize command line parameters over use of scanner;
		//       only prompt for information that isnt provided from the command line

		// this kind of thing is where I'd love to be able to call a non-static from within a static context -_-
		//      because something like hasFileArgs REALLY shouldn't be static
		//      and frankly, im not sure if I want to effectively limit myself to a single instance of this class;
		//          it may not always be the top-level class
		parseArgs(args);
		File file; // = getFile();
		{// Temporarily hardcoded
//			file = new File("D:/msys2_64/dir");
//			file = new File("D:/Projects/Java/Microaud/src/main/resources/TestOgg.ogg");
			file = new File("D:/Projects/Java/Microaud/src/main/resources/TestWav.wav");
			System.out.println("Reading from: " + file.getAbsolutePath());
		}
		doAud(file);

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
