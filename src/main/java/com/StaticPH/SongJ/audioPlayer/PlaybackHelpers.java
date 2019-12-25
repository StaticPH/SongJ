package com.StaticPH.SongJ.audioPlayer;

import com.StaticPH.SongJ.StringUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Vector;

import static com.StaticPH.SongJ.Constants.Colors.FG;
import static com.StaticPH.SongJ.Constants.Colors;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class PlaybackHelpers {
	/**
	 * @param microseconds The duration in microseconds
	 * @return A string representing the duration in hms format.
	 * Includes ms if show_ms is true, or if total duration is less than 1 second.
	 */
	public static String durationToString(long microseconds) {
		// Sorry about the mess of ternary operations nested within string concatenation...
		// Hopefully the parentheses help somewhat
		if (microseconds <= 0) { return "ERROR: Unable to determine duration"; }
		long remainingMS = (microseconds / 1000) % 1000;
		long totalSec = microseconds / (1_000_000);
		if (totalSec == 0) { return remainingMS + "ms";}
		long hours = totalSec / 3600;
		long remainingMins = (totalSec / 60) % 60;
		long remainingSec = totalSec % 60;

		return
			((hours > 0) ? (hours + "h") : (""))
			+ ((remainingMins > 0) ? (remainingMins + "m") : (hours > 0 ? "00m" : ""))
			+ ((remainingSec > 0) ? (remainingSec + "s") : (remainingMins > 0 ? "00s" : ""))
			+ (AbstractAudioPlayer.show_ms ?
			   (remainingMS > 0 ? ((remainingSec == 0 ? "00s" : "") + remainingMS + "ms") : "")
			                               : "");
	}

	public static String duration(long microseconds) { return durationToString(microseconds);}

	/**
	 * Represents the duration of a <tt>Clip</tt> as a <tt>String</tt>
	 *
	 * @return The duration, represented as a <tt>String</tt>.
	 */
	public static String duration(Clip clip) { return durationToString(clip.getMicrosecondLength());}

	/**
	 * Represents the duration of an <tt>AudioInputStream</tt> as a <tt>String</tt>
	 *
	 * @param audIn An <tt>AudioInputStream</tt> to get the duration of.
	 * @param fmt   The format of the <tt>AudioStream</tt>. Used to determine the sampleRate.
	 * @return The duration, represented as a <tt>String</tt>.
	 */
	public static String duration(AudioInputStream audIn, AudioFormat fmt) {
		// fmt.getSampleRate was being cast to double. Static analysis suddenly decided it was unnecessary?
		return durationToString((long) (audIn.getFrameLength() * (1E6 / fmt.getSampleRate())));
	}

	/**
	 * A convenience function for representing the duration of an <tt>AudioInputStream</tt>
	 * as a <tt>String</tt>.<p><br>
	 * <p>
	 * Invoking this method is equivalent to invoking
	 * {@link #duration(AudioInputStream, AudioFormat) duration(audIn, audIn.getFormat())}
	 *
	 * @param audIn An <tt>AudioInputStream</tt> to get the duration of.
	 * @return The duration, represented as a <tt>String</tt>.
	 */
	public static String duration(AudioInputStream audIn) { return duration(audIn, audIn.getFormat());}

	public static void printBar() {
		System.out.println(
			'\n' + FG.BR_CYAN + StringUtils.charNTimes('=', 48) + Colors.DEFAULT + '\n'
		);
	}

	public static void printNowPlaying(AbstractAudioPlayer A, File file) {
		//A.getClass().getName() to show package path too
		System.out.println(
			FG.MAGENTA + "Player: " + FG.BR_MAGENTA + A.getClass().getSimpleName() +
			FG.MAGENTA + " NOW PLAYING \"" + file.getPath() + '"' + Colors.DEFAULT
		);
	}

	public static void printDuration(String len) {
		System.out.println(FG.MAGENTA + "Duration: " + len + Colors.DEFAULT);
	}

	public static String getUnsupportedAudioFileMessage(String filename) {
		return "UnsupportedAudioFileException: \"" + filename + "\" is not a supported audio file.\n" +
		       "If the file extension gives the appearance of a supported file type, " +
		       "verify that its file signature matches the magic bytes for the file type associated with the extension.";
	}

	public static void printPlayQueue(Vector<? extends File> filesToPlay) {
		System.out.print("Queue (Potential media files: " + filesToPlay.size() + "): \n");
		filesToPlay.stream()
		           .map(file -> "      " + file.getPath())
		           .forEach(System.out::println);
		System.out.println();
	}
}
