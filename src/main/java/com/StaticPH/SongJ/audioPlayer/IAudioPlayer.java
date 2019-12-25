package com.StaticPH.SongJ.audioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.util.Set;

// java.util.Collection is a decent conceptual example of an interface class

// Require that all classes implementing this interface define everything herein
@SuppressWarnings("unused")
public interface IAudioPlayer {
	/**
	 * Play a supported type of audio file
	 *
	 * @param file A <tt>File</tt> of any supported file type
	 * @throws UnsupportedAudioFileException If the file is not of a supported type
	 */
	void playFile(File file) throws UnsupportedAudioFileException;

	/**
	 * Each class that implements this interface must implement a method that
	 * returns the file types supported by the class.
	 */
	Set<String> getSupportedTypes();
}
