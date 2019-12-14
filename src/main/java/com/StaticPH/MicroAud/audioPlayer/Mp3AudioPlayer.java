package com.StaticPH.MicroAud.audioPlayer;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static com.StaticPH.MicroAud.AssortedUtils.getLogger;
import static com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers.getUnsupportedAudioFileMessage;

/**
 * TODO: DOCUMENT ME
 */
@SuppressWarnings("unused")
public class Mp3AudioPlayer extends SpecializedAudioPlayer {
	private static final Logger loggo = getLogger("Mp3AudioPlayer");

	public Mp3AudioPlayer() {super(loggo);}

	public Mp3AudioPlayer(int bufferSize) { super(loggo, bufferSize); }

	@Override
	public void playFile(File file) {
		try {
			this.play(AudioSystem.getAudioInputStream(file), mp3Duration(file));
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (UnsupportedAudioFileException e) {
			//TODO: this exception should really be passed all the way up before being caught
			System.out.println(getUnsupportedAudioFileMessage(file.getName()));
		}
	}

	private static long mp3Duration(File f) {
		try {return (long) (new MpegAudioFileReader().getAudioFileFormat(f)).properties().get("duration");}
		catch (UnsupportedAudioFileException | IOException e) { return -1;}
	}
}
