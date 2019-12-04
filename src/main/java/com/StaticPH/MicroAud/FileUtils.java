package com.StaticPH.MicroAud;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class FileUtils {

	private static final Logger loggo = Logger.getLogger(FileUtils.class.getName());

	public static void enableLogging() {loggo.setFilter(record -> true);}    // I hate wizardry like this -_-

	public static void disableLogging() {loggo.setFilter(record -> false);}

	//???: is one of these two fileExists methods somehow better, or more robust?
	public static boolean fileExists(File f) {return f != null && f.exists();}

	public static boolean fileExists(Path p) {return Files.exists(p);}

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


	/**
	 * FIXME: Call me crazy, but I don't think I should be double nesting File instantiation just to avoid handling an IOException
	 * That said, line 638 in {@link com.sun.media.sound.SoftSynthesizer#getDefaultSoundbank} seems to be doing something rather similar?
	 */
	public static File getFileFromPath(String path) {  //TODO: REVAMP ME
/*
		File f = new File(new File(path).getAbsolutePath());

		if (!f.exists()) {
			System.out.println("File not found");
			f = null;
		}
		return f;
*/
		return existingFileOrNull(tryCanonicalize(new File(path)));
	}   //this should probably throw exceptions, not catch them

	public static URL getFileURL(File f) throws MalformedURLException { return f.toURI().toURL();}

	public static boolean isDirectory(File file) {return Files.isDirectory(file.toPath());}

	public static boolean isDirectory(Path path) {return Files.isDirectory(path);}

	public static boolean isDirectory(String path) {return Files.isDirectory(new File(path).toPath());}
//	public static boolean isDirectory(String path){return new File(path).isDirectory();} //???: which of these two methods is better?


	//Look at using an enum/map/dictionary as the type for this function,
	// and assigning a different file type(and unknown) to the different enum values?
	@SuppressWarnings({"ResultOfMethodCallIgnored", "ManualMinMaxCalculation"})
	public static void readNBytes(File file, int numBytes, long offset) {
		//???: Should there be something to prevent numBytes <= 0, and thus internalBufSize <= 0?
//		final int internalBufSize = (numBytes >= 8192) ? numBytes : (numBytes > 256? numBytes: 256); // this would set minimum size as 256
		final int internalBufSize = (numBytes >= 8192) ? numBytes : 8192;
		byte[] buf = new byte[internalBufSize];
		try (InputStream fileStream = new BufferedInputStream(new FileInputStream(file), internalBufSize)) {
			//???: given that InputStream.skip directly returns 0 for negative values, is there any reason to check for them, and throw an exception if found?
			// Even if only to prevent/discourage/punish passing a negative offset to readNBytes?
			fileStream.skip(offset);
			if (fileStream.read(buf) != -1) {
				System.out.println(Arrays.toString(buf));
			}
		}
		catch (IOException e) { e.printStackTrace();}
	}

	public static void readFirstNBytes(File file, int numBytes) { readNBytes(file, numBytes, 0);}

	/**
	 * @see #expandFileList(Collection, boolean, int) expandFileList(Collection&lt;File&gt;, boolean traverse, int maxDepth)
	 * @deprecated Use {@code expandFileList(Collection, boolean, int)} instead
	 */
	@Deprecated
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

//	public static Vector<File> expandFileList(Vector<File> files){ return expandFileList(files, false);}

//	public static Vector<File> expandFileList(Vector<File> files, int maxDepth) {
//		// bad idea when maxDepth defaults to Integer.MAX_VALUE
//		return expandFileList(files, maxDepth > 0, maxDepth);
//	}

	public static Vector<File> expandFileList(Collection<File> files, boolean traverse, int maxDepth) {
//		Vector<Path> paths = new Vector<Path>(files.parallelStream().map(File::toPath));    No clear way to convert a Stream to a Collection in a single statement...
		Vector<File> expandedFiles = new Vector<>();
		Vector<Path> paths = new Vector<>();// Surely there's a way to do this conversion in a single statement...
		files.forEach(file -> paths.add(file.toPath()));    // Always adds ALL file parameters to list of paths to check
		if (traverse) {
//			Stream<Path> pathStream = files.parallelStream().map(File::toPath);
			for (Path path : paths) {   //Check ALL paths in list
//					if (Files.isDirectory(path)){
				try {
					// Add all paths and all child-paths to expanded list
					// It'd be nice to have a copy of this method that only differs by NOT retaining the directories it traversed.
					// Since that doesn't appear to be an option, reduceFiles will have to do that too.
					Files.walk(path, maxDepth, FOLLOW_LINKS)
					     .forEach(walkedPath -> expandedFiles.add(walkedPath.toFile()));
				}
				catch (IOException e) { e.printStackTrace();}
//					}
			}
		}
		else {
			loggo.fine("Directory traversal is not enabled. Directory arguments will be discarded.");
//			paths.removeIf(path -> {
//				if (Files.isDirectory(path)) {
//					// I'd rather this use System.out than print a header line for every discarded directory
//					System.out.println("Discarding \"" + path.toString() + "\"");
//					return true;
//				}
//				return false;
//			});
			paths.forEach(path -> expandedFiles.add(path.toFile()));
		}
		// Clean up the expanded file list before returning it
		return reduceFiles(expandedFiles);
	}

	/**
	 * Removes from a {@code Collection<File>} all elements to which any of these conditions applies:<p>
	 * 1. The <tt>File</tt> cannot be converted to a canonical form <p>
	 * 2. The <tt>File</tt> has a canonical form, but does not exist. <p>
	 * 3. The <tt>File</tt> denotes a directory, and <tt>removeDirs</tt> is true. <p>
	 *
	 * @param allFiles   A {@code Collection<File>} that may contain non-existent Files
	 * @param removeDirs When true, removes all directories from <tt>allFiles</tt>
	 * @return A new {@code Vector<File>} containing only Files known to exist
	 */
	public static Vector<File> reduceFiles(Collection<File> allFiles, final boolean removeDirs) {
		// File elements to be discarded will be set to null first, before all null values are discarded
		Vector<File> files =
			allFiles.stream()
			        /*
			         If the file cannot be canonicalized, discard it
			         */
			        .map(FileUtils::tryCanonicalize)
			        /*
			         If the canonicalized File does not denote an existing file, discard it.
			         Also don't bother checking existence for null Files;
			         even if they somehow exist, they're still getting discarded.
			         */
			        .map(FileUtils::existingFileOrNull)
			        /*
			         If removeDirs is true, discard any Files denoting a directory
			         */
			        .map(f -> ((f != null) && removeDirs && isDirectory(
				        f)) ? null : f) //might be better to just take care of this part during expansion
			        .collect(Collectors.toCollection(Vector::new));
		files.removeIf(Objects::isNull);
		return files;
	}

	/**
	 * Removes from a {@code Collection<File>} all elements to which any of these conditions applies:<p>
	 * 1. The <tt>File</tt> cannot be converted to a canonical form <p>
	 * 2. The <tt>File</tt> has a canonical form, but does not exist. <p><br>
	 *
	 * Invoking this method is equivalent to invoking {@link #reduceFiles(Collection, boolean) reduceFiles(allFiles, true)}
	 *
	 * @param allFiles A {@code Collection<File>} that may contain non-existent Files
	 * @return A new {@code Vector<File>} containing only Files known to exist
	 */
	public static Vector<File> reduceFiles(Collection<File> allFiles) { return reduceFiles(allFiles, true);}

	/*============ HIDDEN PARAMETERS ============*/

	/**
	 * @param file A <tt>File</tt> object
	 * @return The <tt>File</tt> object if it denotes an existing file; otherwise returns <tt>null</tt>.
	 */
	private static File existingFileOrNull(File file) {
		/* No opportunity for feedback
		return file.exists()?file:null;
		*/
		/* not as clear
		if (file != null) {
			if (file.exists()) { return file; }
			loggo.info("No such file: \"" + file.getPath() + "\". This file will be ignored.");
		}
		return null;
		 */
		if (file != null) {
			if (file.exists()) { return file; }
			else {
				loggo.info("No such file: \"" + file.getPath() + "\". This file will be ignored.");
				return null;
			}
		}
		else { return null; }
	}

	/**
	 * @param file A <tt>File</tt> object
	 * @return The canonical equivalent of the <tt>file</tt> parameter, or <tt>null</tt> if an <tt>IOException</tt> occurs.
	 */
	private static File tryCanonicalize(File file) {
		try { return file.getCanonicalFile();}
		catch (IOException e) {
			loggo.info("Unable to get canonical path for \"" + file.getPath() + "\". This file will be ignored.");
			return null;
		}
	}
}

// ???: How can I tell a function that it will never encounter a particular exception??
// ???: Silly question; does making a method final just mean that it can't be overridden?