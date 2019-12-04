package com.StaticPH.MicroAud.audioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
// import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

//import com.StaticPH.MicroAud.AssortedUtils;
import com.StaticPH.MicroAud.StringUtils;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * Converts and plays audio stream (based on conversion
 * support found in the Java audio system)
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ConvertingAudioPlayer extends AbstractAudioPlayer {

	private static final int DEFAULT_BUFFER_SIZE = 65536;

	private final int bufferSize;

	public ConvertingAudioPlayer() { this(DEFAULT_BUFFER_SIZE); }

	public ConvertingAudioPlayer(int bufferSize) { this.bufferSize = bufferSize; }

	@Override
	public void playFile(File file) {
		try {
			play(AudioSystem.getAudioInputStream(file), file.getName());
		}
		catch (IOException e) { e.printStackTrace();}
		catch (UnsupportedAudioFileException e) { System.out.println(getUnsupportedAudioFileMessage(file.getName()));}
	}

	private void play(AudioInputStream audIn, final String name) {
		final AudioFormat outFormat = getOutFormat(audIn.getFormat());
		final Info info = new Info(SourceDataLine.class, outFormat);

		try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {

			if (line != null) {
				line.open(outFormat);
//				AssortedUtils.getLogger().info(
				System.out.println("\033[35mDuration: " + (duration(audIn, outFormat)) + "\033[0m");
				line.start();
				streamAudioOut(getAudioInputStream(outFormat, audIn), line);
				line.drain();
				line.stop();
//				AssortedUtils.getLogger().info(
				System.out.println(
					"ATTN: Finished playing from: \"" + "\033[1;32m" +
					(StringUtils.isNullOrEmpty(name) ? "unknown audio file." : name) +
					"\033[0m" + "\""
				);
			}
		}
		catch (LineUnavailableException | IOException e) { throw new IllegalStateException(e); }
	}

	private static AudioFormat getOutFormat(AudioFormat inFormat) {
		final int ch = inFormat.getChannels();
		final float rate = inFormat.getSampleRate();
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}

	private void streamAudioOut(AudioInputStream in, SourceDataLine line) throws IOException {
		final byte[] buffer = new byte[bufferSize];
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, n);
		}
	}
}

/*
try these:
	get file size; if less than ??7kb?? try to pad the stream, and see if it plays properly after decoding occurs

	create a fallback play method ; within either playFile or playClipFromStream,
	either
	try to force decode the stream to a KNOWN VIABLE format and play that version
	or
	fall back to playing the non-decoded version; print a message apologizing for what will likely be garbled audio

	replace parts of the original library according to the suggested workaround i found on stackexchange

	investigate the possibility of converting the ogg file to an in-memory-only wav file, and play that as usual

	seek out alternative libraries, including MAINTAINED bindings to libraries written in other languages.
*/
