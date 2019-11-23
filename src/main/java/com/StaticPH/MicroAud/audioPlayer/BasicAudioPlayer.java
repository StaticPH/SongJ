package com.StaticPH.MicroAud.audioPlayer;

import com.StaticPH.MicroAud.AssortedUtils;
import com.StaticPH.MicroAud.StringUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

//java.util.Vector is a good conceptual example of a class that extends an abstract class

public class BasicAudioPlayer extends AbstractAudioPlayer{
	/**
	 * Play <tt>.wav</tt>, <tt>.au</tt>, or <tt>.aiff</tt> files, and anything else included in
	 * {@link AudioSystem#getAudioFileTypes()}
	 * <p></p>
	 * Should also support the MIDI based song file formats:
	 * SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 *
	 * @param file A <tt>File</tt> of any supported file type
	 * @see AudioSystem#getAudioFileTypes()
	 */
	public void playFile(File file) {
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
	 * SMF type 0 (Standard MIDI File, aka <tt>.mid</tt> files), SMF type 1 and RMF.
	 *
	 * @param audIn An <tt>InputStream</tt> opened to a file supported by {@link AudioSystem}
	 * @param name  Optional: name of the file audio is being played from
	 * @see #playFile(File)
	 * @see AudioSystem#getAudioFileTypes()
	 */
	public static void playClipFromStream(AudioInputStream audIn, String name) {
		try (Clip clip = AudioSystem.getClip()) {
			clip.open(audIn);
			clip.start();

			if (!StringUtils.isNullOrEmpty(name)) {
				AssortedUtils.getLogger().info("Now playing from: \"" + name + "\"");
			}
			else { AssortedUtils.getLogger().info("Now playing from unknown audio file.");}

			// Main thread still dies before audio starts
//			while (true){
//				if (!clip.isRunning()){
//					break;
//				}
//			}

			// Keeps main thread alive while audio plays

			//TODO: switch to giving each player its own thread, or using a single thread shared between players,
			// but distinct from the program's main thread
			try { Thread.sleep(clip.getMicrosecondLength() / 1000);}
			catch (InterruptedException e) { e.printStackTrace();}
		}
		catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see #playClipFromStream(AudioInputStream, String) playClipFromStream(AudioInputStream, String = null)
	 */
	@Deprecated
	public void playClipFromStream(AudioInputStream audIn) {playClipFromStream(audIn, null);}

/*	public void loopClip(Clip c){
		if (clip.isRunning())
			clip.stop();   // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
		clip.start();     // Start playing
	}*/

}
