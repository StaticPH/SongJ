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

import static com.StaticPH.MicroAud.AssortedUtils.doze;
import static com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers.getUnsupportedAudioFileMessage;

//java.util.Vector is a good conceptual example of a class that extends an abstract class

@SuppressWarnings({"unused", "WeakerAccess"})
public class BasicAudioPlayer extends AbstractAudioPlayer {
	public static final Logger loggo = AssortedUtils.getLogger("BasicAudioPlayer");

	public BasicAudioPlayer() {}

	/* I dont think I really WANT this to be static?
		feels like doing so would risk the program attempting to play all files at once or something equally undesirable
		(Which I MIGHT still like to be POSSIBLE by giving each instance of a player its own playback thread,
		as opposed to having all players of a particular type share 1, or all players of all types)

		IDEALLY I'd be able to somehow instruct the program to use a particular implementation
		???:Some sort of factory perhaps?
	 */

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
	public void playFile(File file) {
		/**
		 * Throws IOException by way of <tt>reader.getAudioInputStream(file)</tt>
		 * at line 1181 of {@link AudioSystem#getAudioInputStream(File)}
		 * That function (reader.getAudioInputStream) is then defined in different FileReader implementations
		 */
		try { playClipFromStream(AudioSystem.getAudioInputStream(file));}
		catch (IOException e) { e.printStackTrace();}
		catch (UnsupportedAudioFileException e) {
			//TODO: this exception should really be passed all the way up before being caught
			System.out.println(getUnsupportedAudioFileMessage(file.getName()));
		}
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
			System.out.println("Duration: " + PlaybackHelpers.duration(clip.getMicrosecondLength()) + '\n');
			clip.start();

			//TODO: switch to giving each player its own thread, or using a single thread shared between players,
			// but distinct from the program's main thread

			// Keeps main thread alive while audio plays
			doze(clip.getMicrosecondLength() / 1000);
		}
		catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

/*	public void loopClip(Clip c){
		if (clip.isRunning())
			clip.stop();   // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
		clip.start();     // Start playing
	}*/

}
