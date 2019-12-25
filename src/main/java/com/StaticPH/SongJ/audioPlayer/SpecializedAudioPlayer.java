package com.StaticPH.SongJ.audioPlayer;

import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

import static com.StaticPH.SongJ.audioPlayer.PlaybackHelpers.printDuration;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

@SuppressWarnings("WeakerAccess")
public abstract class SpecializedAudioPlayer extends AbstractAudioPlayer {
	private final Logger loggo;

	private static final int DEFAULT_BUFFER_SIZE = 65536;

	private final int bufferSize;

	public SpecializedAudioPlayer(Logger logger) {
		this(logger, DEFAULT_BUFFER_SIZE);
	}

	public SpecializedAudioPlayer(Logger logger, int bufferSize) {
		this.loggo = logger;
		this.bufferSize = bufferSize;
	}

	protected static AudioFormat getOutFormat(AudioFormat inFormat) {
		final int ch = inFormat.getChannels();
		final float rate = inFormat.getSampleRate();
		// sampleRate expected to be one of 8000, 11025, 16000, 22050, 44100
		// sampleSizeInBits expected to be either 8 or 16
		// channels expected to be either 1 or 2 (mono or stereo, respectively)
		// Typically frameRate = sampleRate
		// See javax.sound.sampled.AudioFormat:250
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}

	protected void streamAudioOut(AudioInputStream in, SourceDataLine line) throws IOException {
		final byte[] buffer = new byte[this.bufferSize];
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			// The write call needs to occur regardless of logging state,
			// so don't use a lambda to evaluate lazily
			this.loggo.debug("read {} bytes\nwrote {} bytes", n, line.write(buffer, 0, n));
		}
	}

	protected void play(AudioInputStream audIn, long fileDuration) {
		final AudioFormat outFormat = getOutFormat(audIn.getFormat());
		final String durationAsString = (
			fileDuration != -1 ? PlaybackHelpers.duration(fileDuration)
			                   : PlaybackHelpers.duration(audIn, outFormat)
		);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

		try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
			this.loggo.debug("Max available Bytes = {}", audIn.available());
			if (line != null) {
				line.open(outFormat);

				printDuration(durationAsString);

				line.start();

				this.streamAudioOut(getAudioInputStream(outFormat, audIn), line);

				line.drain();
				line.stop();
			}
		}
		catch (LineUnavailableException | IOException e) { throw new IllegalStateException(e); }
	}
}