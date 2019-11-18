package com.StaticPH.MicroAud;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class FileUtils {

	private static final Logger loggo = Logger.getLogger(FileUtils.class.getName());

	public static boolean fileExists(File f) {return f != null && f.exists();}

	@Deprecated
	public static File getFileFromScanner() {
		File f;

		try (Scanner userInput = new Scanner(System.in)) {
			f = new File(userInput.next()).getCanonicalFile();
//			f = f.getCanonicalFile();
			loggo.info(
//				String.format("'%s' resolved to canonical path: '%s'\n", f.getPath(), f.getCanonicalPath()) // String.format performs poorly
				'\'' + f.getPath() + "' resolved to canonical path: '" + f.getCanonicalPath() + "'\n"
			);
			if (!f.exists()) {
				loggo.info("File not found: '" + f.getCanonicalPath() + "'\n");
				f = null;
			}
		}
		catch (IOException e) {
			loggo.log(Level.FINER, "Error constructing canonical pathname", e);
			f = null;
		}
		return f;
//		return f.exists() ? f : null;
//		return fileExists(f) ? f :null;
	}

	//FIXME: Call me crazy, but I don't think I should be double nesting File instantiation just to avoid handling an IOException

	/**
	 * That said, line 638 in {@link com.sun.media.sound.SoftSynthesizer#getDefaultSoundbank} seems to be doing something rather similar?
	 */
	public static File getFileFromPath(String path) {  //TODO: REVAMP ME
		File f = new File(new File(path).getAbsolutePath());

		if (!f.exists()) {
			System.out.println("File not found");
			f = null;
		}
		return f;
	}   //this should probably throw exceptions, not catch them

	public static URL getFileURL(File f) throws MalformedURLException { return f.toURI().toURL();}

	public static boolean isDirectory(String path) {return Files.isDirectory(new File(path).toPath());}
//	public static boolean isDirectory(String path){return new File(path).isDirectory();} //???: which method is better?

	//FIXME: Surely there's a better way?
	public static Vector<String> buildDirTree(String startDir) {
		if (!isDirectory(startDir)) { return null;}
		Vector<String> tree = new Vector<>();
		try {
			Files.walk(new File(startDir).toPath(), Integer.MAX_VALUE, FOLLOW_LINKS)
//			     .filter(REMOVE NULL AND EMPTY)
//			     .filter(REMOVE UNSUPPORTED FILE TYPES)
//			     .limit(SOME SANE VALUE, LIKE 20, because its unlikely that a user will actually want to play that many things in a queue without a means of pausing or skipping)
//                 .forEach(
//	                 path -> System.out.println("walking: " + path)
//                 );
                 .forEach(path -> tree.add(path.toString()))
			;

		}
		catch (IOException e) {
			System.out.println("exception at walk: " + e);
		}
		return tree;
	}
}
