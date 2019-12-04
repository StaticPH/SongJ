package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;

// java.util.Collection is a decent conceptual example of an interface class

// Require that all classes implementing this interface define everything herein
public interface IAudioPlayer {
	//???: defining class fields(member variables) in interfaces? or abstract classes?
	void playFile(File file) throws UnsupportedAudioFileException;

	/*
		/**
		 * @param u_sec The duration of a file in microseconds
		 * @return A <tt>String</tt> representing the duration in a form more easily understood by a human
		 * /
		String durationToString(long u_sec); //u_sec is in microseconds
	*/

	/*
		/**
		 * @param u_sec The duration of a file in microseconds
		 * @param show_ms If true, include milliseconds in the returned <tt>String</tt>
		 * @return A <tt>String</tt> representing the duration in a form more easily understood by a human
		 * /
		String durationToString(long u_sec, boolean show_ms);
	*/
}
