package com.StaticPH.MicroAud.audioPlayer;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static com.StaticPH.MicroAud.AssortedUtils.getLogger;

/**
 * TODO: DOCUMENT ME
 */
@SuppressWarnings("unused")
public class Mp3AudioPlayer extends SpecializedAudioPlayer {
	private static final Logger loggo = getLogger("Mp3AudioPlayer");

	// The faster approach is to just check file extensions, but extensions dont have to match the file's actual contents...
	//	protected static final Set<String> supportedTypes = Collections.singleton(".mp3");

	//Populate with the content-type of supported files
	//TODO: Try with mp2, m1a, mpx, mxa, aac, mpa, m2a, m25a... I dont even know how many of these are distinct file types that need to be tested for
	protected static final Set<String> supportedTypes = Collections.singleton("AUDIO_MPEG");

	@Override
	public Set<String> getSupportedTypes() { return supportedTypes;}

	static { PlayerTypeMap.insert(new Mp3AudioPlayer(), supportedTypes);}


	public Mp3AudioPlayer() {super(loggo);}

	public Mp3AudioPlayer(int bufferSize) { super(loggo, bufferSize); }

	@Override
	public void playFile(File file) throws UnsupportedAudioFileException {
		try {
			this.play(AudioSystem.getAudioInputStream(file), mp3Duration(file));
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	private static long mp3Duration(File f) {
		try {return (long) (new MpegAudioFileReader().getAudioFileFormat(f)).properties().get("duration");}
		catch (UnsupportedAudioFileException | IOException e) { return -1;}
	}
}
