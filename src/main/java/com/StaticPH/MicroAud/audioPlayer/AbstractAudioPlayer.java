package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.util.Set;


// java.util.AbstractCollection is a decent conceptual example of an abstract class;

/**
 * This class provides a skeletal implementation of the <tt>IAudioPlayer</tt>
 * interface, to minimize the effort required to implement this interface.
 * <p>
 * To implement an AudioPlayer, the programmer needs only to extend this
 * class and provide an implementation for the <tt>playFile</tt> methods.
 * <p>
 * The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if the
 * audio player being implemented provides a more efficient/specialized
 * implementation.
 */
@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public abstract class AbstractAudioPlayer implements IAudioPlayer {

	/** When true, duration strings include milliseconds. Rounds down to nearest second when false */
	protected static boolean show_ms = false;

//  Temporarily disabled on account of apparent inconsistency of results including ms
//	/** Enable or disable showing milliseconds in <tt>String</tt> representations of file duration. */
//	public final void setShow_ms(boolean show_ms) { this.show_ms = show_ms;}

	/** Sole constructor.  (For invocation by subclass constructors, typically implicit.) */
	protected AbstractAudioPlayer() {} // Not really meaningful unless an extending Player requires a constructor with a parameter

	@Override
	public abstract void playFile(File file) throws UnsupportedAudioFileException;

	/** @return The file types supported by the specific subclass of AbstractAudioPlayer */
	@Override
	public abstract Set<String> getSupportedTypes();
}
