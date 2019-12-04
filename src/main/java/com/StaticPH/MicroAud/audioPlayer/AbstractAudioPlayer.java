package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;


// java.util.AbstractCollection is a decent conceptual example of an abstract class;
//
// AbstractAudioPlayer wouldn't be much of a skeletal implementation currently if not for
// duration helpers being implemented here

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
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractAudioPlayer implements IAudioPlayer {
	/*
	???: TODO: Do i make this extend some arbitrary type so that I can create ALL players like
		AnyPlayer player = specificPlayer();
		or what? WAIT, ISN'T THAT WHAT I'M DOING ALREADY?
	 */

	/** When true, duration strings include milliseconds. Rounds down to nearest second when false */
	private boolean show_ms = false;

	/** Enable or disable showing milliseconds in <tt>String</tt> representations of file duration. */
	public final void setShow_ms(boolean show_ms) { this.show_ms = show_ms;}

	/** Sole constructor.  (For invocation by subclass constructors, typically implicit.) */
	protected AbstractAudioPlayer() {} // Not really meaningful unless an extending Player requires a constructor with a parameter

	@Override
	public abstract void playFile(File file) throws UnsupportedAudioFileException;

	final String getUnsupportedAudioFileMessage(String filename){
		return "UnsupportedAudioFileException: \"" + filename + "\" is not a supported audio file.\n" +
//		return "\"" + filename + "\" is not a supported audio file.\n" +
               "If the file extension gives the appearance of a supported file type, " +
               "verify that its file signature matches the magic bytes for the file type associated with the extension.";
	}

	/**
	 * @param u_sec The duration in microseconds
	 * @return A string representing the duration in hms format. Includes ms if show_ms is true
	 */
//	@SuppressWarnings("SameParameterValue")
	public String durationToString(long u_sec) {
		// Sorry about the mess of ternary operations nested within string concatenation...
		// Hopefully the parentheses help somewhat
		if (u_sec <= 0) { return "ERROR"; }
		long remainingMS = (u_sec / 1000) % 1000;
		long totalSec = u_sec / (1_000_000);
		long hours = totalSec / 3600;
		long remainingMins = (totalSec / 60) % 60;
		long remainingSec = totalSec % 60;
		return
			((hours > 0) ? (hours + "h") : (""))
			+ ((remainingMins > 0) ? (remainingMins + "m") : (hours > 0 ? "00m" : ""))
			+ ((remainingSec > 0) ? (remainingSec + "s") : "")
			+ (show_ms ?
			   (remainingMS > 0 ? ((remainingSec == 0 ? "00s" : "") + remainingMS + "ms") : "")
			           : "");
	}

	/** Represents the duration of a <tt>Clip</tt> as a <tt>String</tt>
	 * @return The duration, represented as a <tt>String</tt>. */
	public String duration(Clip clip) { return durationToString(clip.getMicrosecondLength());}

	/** Represents the duration of an <tt>AudioInputStream</tt> as a <tt>String</tt>
	 * @param audIn An <tt>AudioInputStream</tt> to get the duration of.
	 * @param fmt The format of the <tt>AudioStream</tt>. Used to determine the sampleRate.
	 * @return The duration, represented as a <tt>String</tt>.
	 */
	public String duration(AudioInputStream audIn, AudioFormat fmt) {
		return durationToString((long) (audIn.getFrameLength() * (1E6 / (double) fmt.getSampleRate())));
	}
	/** A convenience function for representing the duration of an <tt>AudioInputStream</tt>
	 * as a <tt>String</tt>.<p><br>
	 *
	 * Invoking this method is equivalent to invoking
	 * {@link #duration(AudioInputStream, AudioFormat) duration(audIn, audIn.getFormat())}
	 *
	 * @param audIn An <tt>AudioInputStream</tt> to get the duration of.
	 * @return The duration, represented as a <tt>String</tt>.
	 */
	public String duration(AudioInputStream audIn) { return duration(audIn, audIn.getFormat());}
}
