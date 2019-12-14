package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;

// java.util.Collection is a decent conceptual example of an interface class

// Require that all classes implementing this interface define everything herein
public interface IAudioPlayer {
	//???: defining class fields(member variables) in interfaces? or abstract classes?

	/** Play a supported type of audio file
	 * @param file A <tt>File</tt> of any supported file type
	 * @throws UnsupportedAudioFileException If the file is not of a supported type
	 */
	void playFile(File file) throws UnsupportedAudioFileException;
}
