package com.StaticPH.MicroAud.audioPlayer;

import java.io.File;


// java.util.AbstractCollection is a decent conceptual example of an abstract class;
// AbstractAudioPlayer isn't much of a skeletal implementation as it currently doesn't
// actually provide implementations for anything

/**
 * This class provides a skeletal implementation of the <tt>IAudioPlayer</tt>
 * interface, to minimize the effort required to implement this interface. <p>
 *
 * To implement an AudioPlayer, the programmer needs only to extend this
 * class and provide an implementation for the <tt>playFile</tt> methods. <p>
 *
 * The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if the
 * audio player being implemented provides a more efficient/specialized
 * implementation.<p>
 */
public abstract class AbstractAudioPlayer implements IAudioPlayer{
	/**
	 * Sole constructor.  (For invocation by subclass constructors, typically
	 * implicit.)
	 */
//	protected AbstractAudioPlayer(){} // Not really meaningful unless an extending Player requires a constructor with a parameter

	public abstract void playFile(File file);
}
