package com.StaticPH.MicroAud.audioPlayer;

import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.StaticPH.MicroAud.AssortedUtils.getLogger;
import static com.StaticPH.MicroAud.AssortedUtils.getNullBytes;

/**
 * TODO: DOCUMENT ME
 */
@SuppressWarnings({"unused", "WeakerAccess", "LocalCanBeFinal"})
public final class VorbisAudioPlayer extends SpecializedAudioPlayer {
	private static final Logger loggo = getLogger("VorbisAudioPlayer");

	// The faster approach is to just check file extensions, but extensions dont have to match the file's actual contents...
	//	protected static final Set<String> supportedTypes = new HashSet<>(Arrays.asList(".ogg", ".oga"));

	//Populate with the content-type of supported files
	// TODO: Find out if this will also properly handle flac, theora, speex, and opus files
	protected static final Set<String> supportedTypes = new HashSet<>(Arrays.asList("OGG"));

	@Override
	public Set<String> getSupportedTypes() { return supportedTypes;}

	static { PlayerTypeMap.insert(new VorbisAudioPlayer(), supportedTypes);}


	public VorbisAudioPlayer() { super(loggo); }

	public VorbisAudioPlayer(int bufferSize) { super(loggo, bufferSize);}

	private static final int tooShort = 6145; // You must be at least this tall...er, long, to ride the ride.

	/*
	There seems to be some kind of issue with either vorbisspi or jorbis making it so
	that anything shorter than 6145 bytes in total size completely refuses to play, and the
	only way I managed to work around that was by padding the file to at least that size.
	That workaround occurs as part of playFile, and is carried out by padTempFile.
	My initial attempt at simply feeding null bytes to the Line (which ultimately writes to
	the AudioInputSteam) was a failure, but it would be a preferable solution to the current
	workaround, which requires copying the entire file. Granted that only occurs when the file
	is already less than 6145 bytes, but it'd be even better if it didnt happen at all.
	TODO: TRICK THIS INTO THINKING ITS THAT LONG WITHOUT ACTUALLY MODIFYING ANY AUDIO FILES
	// System.out.println(audIn.available());
	if (audIn.available() < 6145) {
		System.out.println("Available bytes < 6145");
	 //	byte[] filler = new byte[6145];
		byte[] filler = new byte[6145 - availableBytes];
		Arrays.fill(filler, (byte) 0);
		System.out.println(line.write(filler, 0, 6144));
	}
	*/

	private static File padTempFile(File file) throws IOException {
		final String name = file.getName();

		// Will create a tmp file in wherever the user's standard tmp file location is
		// Why bother trying to name it something when the function will add nonsense to it?
		// Just call it something arbitrary, because it will be deleted soon anyways.
		File newFile = File.createTempFile("abc123", name.substring(name.lastIndexOf('.')));
		newFile.deleteOnExit();

		try (OutputStream out = new FileOutputStream(newFile)) {
			Files.copy(file.toPath(), out);
			out.write(
				getNullBytes((int) (tooShort - file.length()))
			);
		}
		return newFile;
	}

	@Override
	public void playFile(File file) throws UnsupportedAudioFileException {
		loggo.debug("file length = {}", file::length);

		try {
			// And now it's necessary again. I don't even...
			File useFile = file.length() >= tooShort ? file : padTempFile(file);
			this.play(AudioSystem.getAudioInputStream(useFile), vorbisDuration(file.getPath()));
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	/**
	 * @param filePath A <tt>String</tt> representing the path to a known-to-exist file;
	 *                 the file is assumed to be a vorbis file
	 * @return The duration in microseconds of the vorbis file denoted by <tt>filePath</tt>,
	 * or -1 if an exception occurred.
	 */
	public static long vorbisDuration(final String filePath) {
		// If there were no problems, multiply return value by 1 million to convert from seconds to microseconds
		try { return (long) (new VorbisFile(filePath).time_total(-1) * 1_000_000); }
		catch (JOrbisException e) { return -1; }
	}

}
