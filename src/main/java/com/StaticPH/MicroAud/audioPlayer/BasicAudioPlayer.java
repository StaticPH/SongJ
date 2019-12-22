package com.StaticPH.MicroAud.audioPlayer;

import com.StaticPH.MicroAud.AssortedUtils;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.StaticPH.MicroAud.AssortedUtils.doze;
import static com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers.*;

//java.util.Vector is a good conceptual example of a class that extends an abstract class

@SuppressWarnings({"unused", "WeakerAccess"})
public class BasicAudioPlayer extends AbstractAudioPlayer {
	public static final Logger loggo = AssortedUtils.getLogger("BasicAudioPlayer");


	// The faster approach is to just check file extensions, but extensions dont have to match the file's actual contents...
	//	protected static final Set<String> supportedTypes = new HashSet<>(Arrays.asList(".wav", ".au", ".aiff", ".mid"));

	//Populate with the content-type of supported files
	// TODO: test playback and quality of aifc, 8svx, MThd, MTrk, and anything else that .aiff, .au, and .mid can be
	protected static final Set<String> supportedTypes = new HashSet<>(Arrays.asList("WAV", "AIFF"));

	@Override
	public Set<String> getSupportedTypes() { return supportedTypes;}

	static { PlayerTypeMap.insert(new BasicAudioPlayer(), supportedTypes);}


	public BasicAudioPlayer() {}

	/**
	 * Play <tt>.wav</tt>, <tt>.au</tt>, or <tt>.aiff</tt> files, and anything else included in
	 * {@link AudioSystem#getAudioFileTypes()}
	 * <p>
	 * Should also support the MIDI based song file formats:
	 * SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 *
	 * @param file A <tt>File</tt> of any supported file type
	 * @see AudioSystem#getAudioFileTypes()
	 */
	@SuppressWarnings("DanglingJavadoc")
	@Override
	public void playFile(File file) throws UnsupportedAudioFileException {
		/**
		 * Throws IOException by way of <tt>reader.getAudioInputStream(file)</tt>
		 * at line 1181 of {@link AudioSystem#getAudioInputStream(File)}
		 * That function (reader.getAudioInputStream) is then defined in different FileReader implementations
		 */
		//TODO: Maybe this should be converted to use a SourceDataLine like the other two...
		try { playClipFromStream(AudioSystem.getAudioInputStream(file));}
		catch (IOException e) { e.printStackTrace();}
	}

	/**
	 * Play audio from an <tt>InputStream</tt> opened to <tt>.wav</tt>, <tt>.au</tt>, or <tt>.aiff</tt>
	 * files, or any other file type supported
	 * according to {@link AudioSystem#getAudioFileTypes()}
	 * <p>
	 * Should also support an <tt>InputStream</tt> opened to MIDI based song file formats:
	 * SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 *
	 * @param audIn An <tt>InputStream</tt> opened to a file supported by {@link AudioSystem}
	 * @see #playFile(File)
	 * @see AudioSystem#getAudioFileTypes()
	 */
	public static void playClipFromStream(AudioInputStream audIn) {
		try (Clip clip = AudioSystem.getClip()) {
			clip.open(audIn);
			printDuration(duration(clip.getMicrosecondLength()));
			clip.start();

			// Keeps main thread alive while audio plays
			doze(clip.getMicrosecondLength() / 1000);
		}
		catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

}
