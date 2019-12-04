package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;


// java.util.AbstractCollection is a decent conceptual example of an abstract class;
// AbstractAudioPlayer isn't much of a skeletal implementation as it currently doesn't
// actually provide implementations for anything

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
 * implementation.<p>
 */
@SuppressWarnings("unused")
public abstract class AbstractAudioPlayer implements IAudioPlayer {
	/*
	???: TODO: Do i make this extend some arbitrary type so that I can create ALL players like
		AnyPlayer player = specificPlayer();
	or what?
	 */

	private boolean show_ms = false; // When true, duration strings include milliseconds

	public final void setShow_ms(boolean show_ms) { this.show_ms = show_ms;}

	/**
	 * Sole constructor.  (For invocation by subclass constructors, typically
	 * implicit.)
	 */
//	protected AbstractAudioPlayer(){} // Not really meaningful unless an extending Player requires a constructor with a parameter
	public abstract void playFile(File file) throws UnsupportedAudioFileException;

	final String getUnsupportedAudioFileMessage(String filename){
		return "UnsupportedAudioFileException: \"" + filename + "\" is not a supported audio file.\n" +
//		return "\"" + filename + "\" is not a supported audio file.\n" +
               "If the file extension gives the appearance of a supported file type, " +
               "verify that its file signature matches the magic bytes for the file type associated with the extension.";
	}

//	public final String durationToString(long u_sec) { return durationToString(u_sec, false);}

	/**
	 * @param u_sec duration in microseconds
	 * @return A string representing the duration in hms format. Includes ms if show_ms is true
	 */
	@SuppressWarnings("SameParameterValue")
	private String durationToString(long u_sec) { //, boolean show_ms) {
		// Sorry about the mess of ternary operations nested within string concatenation...
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

	public String duration(Clip clip) { return durationToString(clip.getMicrosecondLength());}

	public String duration(AudioInputStream audIn, AudioFormat fmt) {
		return durationToString(
			(long) (audIn.getFrameLength() * (1E6 / (double) fmt.getSampleRate()))
		);
	}

	public String duration(AudioInputStream audIn) { return duration(audIn, audIn.getFormat());}
}
