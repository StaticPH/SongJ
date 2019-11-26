package com.StaticPH.MicroAud.audioPlayer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


@SuppressWarnings({"unused", "WeakerAccess", "InnerClassMayBeStatic"})
public class Mp3AudioPlayer extends AbstractAudioPlayer {

	public Mp3AudioPlayer() {}

//	private String nowPlaying;
//
//	public String getNowPlaying() { return nowPlaying; }

	@Override
	public void playFile(File file) {
		/* Regarding FileNotFoundExceptions:
		 Ultimately, the FileNotFoundException should not occur IF the File object parameter came from a Vector<File>
		 returned by FileUtils.reduceFiles (and by extension, FileUtils.expandFileList), as all File objects within that
		 Vector are known to have existed at the time the function returned. More accurately speaking, they were all
		 checked for existence during the function; can't discount the possibility of something changing between
		 the time they were checked and the time this function is called. But unless something is moving or deleting
		 the files already in the playback queue, this shouldn't be a problem.
		 */
		InfoListener listener = new InfoListener();
		playMp3File(file, 0, Integer.MAX_VALUE, listener);
	}


	public void playMp3File(File mp3, int start, int end, ExtendedPlaybackListener listener) {
		try {
			final MoreAdvancedPlayer player = new MoreAdvancedPlayer(mp3);
			player.setPlayBackListener(listener);
			new Thread(() -> {
				try { player.play(start, end); }
				catch (JavaLayerException e) { e.printStackTrace(); }
			}).start();

		}
		catch (JavaLayerException | FileNotFoundException e) { e.printStackTrace(); }
	}

	public class InfoListener extends ExtendedPlaybackListener {
		//FIXME: None of this works (probably for more than just this reason) because
		// AdvancedPlayer.play() references PlaybackEvent rather than ExtendedPlaybackEvent
		// I suspect that also means that MoreAdvancedPlayer has no way of knowing when playback is finished
		// That would likely preclude me from making MicroAud wait until the mp3 song finishes before playing
		// the next thing from the queue. Shame JLayer seems to lack a means of determining the song length until it
		// reaches it
		public void playbackStarted(ExtendedPlaybackEvent evt) {
			final int startPoint = evt.getFrame();
			if (startPoint == 0) {
				System.out.println("Playing \"" + ((MoreAdvancedPlayer) evt.getSource()).getPlayingFile() + "\"");
				//???: Will casting the Source back to a MoreAdvancedPlayer allow access to the field value
				// set in its(MoreAdvancedPlayer's) constructor?
			}
			else {
				System.out.println(
					"Started playing \"" +
					((MoreAdvancedPlayer) evt.getSource()).getPlayingFile() +
					"\"from frame " + startPoint
				);
			}
		}

		public void playbackFinished(ExtendedPlaybackEvent evt) {
			System.out.println("Play completed at frame " + evt.getFrame());

		}

		public void playbackInterrupted(ExtendedPlaybackEvent evt) {
			System.out.println("Play interrupted at frame " + evt.getFrame());
		}

		public void playbackResumed(ExtendedPlaybackEvent evt) {
			System.out.println("Play resumed from frame " + evt.getFrame());
		}
	}
}

@SuppressWarnings("unused")
class ExtendedPlaybackEvent extends PlaybackEvent {
	public static int INTERRUPTED = 3; //TODO: USE THESE
	public static int RESUMED = 4;

	public ExtendedPlaybackEvent(MoreAdvancedPlayer source, int id, int frame) { super(source, id, frame); }
}

//TODO: INCORPORATE THESE SOMEHOW
@SuppressWarnings("unused")
abstract class ExtendedPlaybackListener extends PlaybackListener {
	public void playbackInterrupted(PlaybackEvent event) {}

	public void playbackResumed(PlaybackEvent event) {}
}

@SuppressWarnings("WeakerAccess")
class MoreAdvancedPlayer extends AdvancedPlayer {
	private String playingFile;

	MoreAdvancedPlayer(File file) throws FileNotFoundException, JavaLayerException {
		this(new BufferedInputStream(new FileInputStream(file)));
		playingFile = file.getPath();
	}

	MoreAdvancedPlayer(InputStream is) throws JavaLayerException { super(is); }

	public String getPlayingFile() { return playingFile; }
}
