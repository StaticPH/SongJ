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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static com.StaticPH.MicroAud.AssortedUtils.getLogger;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class FileUtils {

	private static final Logger loggo = getLogger("FileUtils");

	//???: is one of these two fileExists methods somehow better, or more robust?
	public static boolean fileExists(File f) {return f != null && f.exists();}

	public static boolean fileExists(Path p) {return Files.exists(p);}

	@Deprecated
	public static File getFileFromScanner() {
		File f;

		try (Scanner userInput = new Scanner(System.in)) {
			f = new File(userInput.next()).getCanonicalFile();
			loggo.debug("'{}' resolved to canonical path: '{}'", f.getPath(), f.getCanonicalPath());
			if (!f.exists()) {
				loggo.debug("File not found: '{}'\n", f.getCanonicalPath());
				f = null;
			}
		}
		catch (IOException e) {
			loggo.warn("Error constructing canonical pathname", e);
			f = null;
		}
		return f;
//		return f.exists() ? f : null;
//		return fileExists(f) ? f :null;
	}


	public static File getFileFromPath(String path) {
		return existingFileOrNull(tryCanonicalize(new File(path)));
	}

	public static URL getFileURL(File f) throws MalformedURLException { return f.toURI().toURL();}

	public static File getWorkingDir() { return new File(".").getAbsoluteFile();}

	public static String getWorkingDirPath() { return new File(".").getAbsolutePath();}


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

	public static Vector<File> expandFileList(Collection<? extends File> files, boolean traverse, int maxDepth) {
		Vector<File> expandedFiles = new Vector<>();
		Vector<Path> paths = files.stream().map(File::toPath).collect(Collectors.toCollection(Vector::new));
		// Always adds ALL file parameters to list of paths to check
		if (traverse) {
			for (Path path : paths) {   //Check ALL paths in list
//				paths.stream().flatMap(???);  I'm all but certain this can help here, but I can't see how
				try (Stream<Path> walked = Files.walk(path, maxDepth, FOLLOW_LINKS)) {
					expandedFiles.addAll(walked.map(Path::toFile)
					                           .collect(Collectors.toCollection(Vector::new))
					);
				}
				catch (IOException e) { e.printStackTrace();}
			}
		}
		else {
			loggo.info("Directory traversal is not enabled. Directory arguments will be discarded.");
			expandedFiles.addAll(
				paths.stream()
				     .map(Path::toFile)
				     .filter(f -> !f.isDirectory())
				     .collect(Collectors.toCollection(Vector::new))
			);
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
	public static Vector<File> reduceFiles(Collection<? extends File> allFiles, boolean removeDirs) {
		// File elements to be discarded will be set to null first, before all null values are discarded
		Vector<File> files =
			allFiles.stream()
			        /* If the file cannot be canonicalized, discard it */
			        .map(FileUtils::tryCanonicalize)
			        /*
			         If the canonicalized File does not denote an existing file, discard it.
			         Also don't bother checking existence for null Files;
			         even if they somehow exist, they're still getting discarded.
			         */
			        .map(FileUtils::existingFileOrNull)
			        /* If removeDirs is true, discard any Files denoting a directory */
			        .map(f -> ((f != null) && removeDirs && isDirectory(f)) ? null : f)
			        //Should this last map() call just be taken care of during expansion?
			        .collect(Collectors.toCollection(Vector::new));
		files.removeIf(Objects::isNull);
		return files;
	}

	/**
	 * Removes from a {@code Collection<File>} all elements to which any of these conditions applies:<p>
	 * 1. The <tt>File</tt> cannot be converted to a canonical form <p>
	 * 2. The <tt>File</tt> has a canonical form, but does not exist. <p><br>
	 * <p>
	 * Invoking this method is equivalent to invoking {@link #reduceFiles(Collection, boolean) reduceFiles(allFiles, true)}
	 *
	 * @param allFiles A {@code Collection<File>} that may contain non-existent Files
	 * @return A new {@code Vector<File>} containing only Files known to exist
	 */
	public static Vector<File> reduceFiles(Collection<? extends File> allFiles) { return reduceFiles(allFiles, true);}

	/*============ HIDDEN PARAMETERS ============*/

	/**
	 * @param file A <tt>File</tt> object
	 * @return The <tt>File</tt> object if it denotes an existing file; otherwise returns <tt>null</tt>.
	 */
	private static File existingFileOrNull(File file) {
		if (file != null) {
			if (file.exists()) { return file; }
			else {
				loggo.info("No such file: \"{}\". This file will be ignored.", file::getPath);
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
			loggo.warn("Unable to get canonical path for \"{}\". This file will be ignored.", file::getPath);
			return null;
		}
	}
}

// ???: How can I tell a function that it will never encounter a particular exception??
// ???: Silly question; does making a method final just mean that it can't be overridden?