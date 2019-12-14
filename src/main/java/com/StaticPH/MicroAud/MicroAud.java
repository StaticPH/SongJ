package com.StaticPH.MicroAud;

import com.StaticPH.MicroAud.audioPlayer.AbstractAudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.BasicAudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.Mp3AudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers;
import com.StaticPH.MicroAud.audioPlayer.VorbisAudioPlayer;
import com.StaticPH.MicroAud.cli.ArgManager;
import com.StaticPH.MicroAud.cli.CLIArguments;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.nio.file.Path;
import java.util.Vector;

import static com.StaticPH.MicroAud.AssortedUtils.printBar;
import static com.StaticPH.MicroAud.StringUtils.delimitStrings;


@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class MicroAud {
	// ======= Class (Static) Variables ======= //
	private static final Logger loggo = AssortedUtils.getLogger("MicroAud");

	// ======= Instance (Non-Static) Variables ======= //

	// ======= Class Constructors ======= //
	public MicroAud() {}

	// ======= Methods ======= //

	public static void logArgs(String[] args) { loggo.info(delimitStrings(",", true, "args: [", "]", args));}

	public static void printPlayQueue(Vector<? extends File> filesToPlay) {
		//Could technically use a Collection<File> instead
		System.out.println("Queue" + "(length prior to type filtering: " + filesToPlay.size() + "): \n");
		filesToPlay.stream()
//		           .filter(file -> {
//			           return file.getPath().endsWith(".wav");
//		           }) //TODO: REMOVE ME!! I LIMIT THE OUTPUT TO WAV FILES
                   .map(file -> "      " + file.getPath())
                   .forEach(System.out::println);
	}

	public static void doAud(File file) {
		try {
			AbstractAudioPlayer a;
			//FIXME: TEMPORARY! REPLACE WITH FACTORY ASAP!
			if (file.getPath().endsWith(".mp3")) {
				a = new Mp3AudioPlayer();
			}
			else if (file.getPath().endsWith(".ogg")) {
				a = new VorbisAudioPlayer();
			}
			else {
				a = new BasicAudioPlayer();
			}
			PlaybackHelpers.nowPlaying(a, file);
			a.playFile(file);
			System.out.println("\033[1;32mFinished playing from " + file.getPath() + "\033[0m\n");
			printBar();
		}
		catch (UnsupportedAudioFileException e) { e.printStackTrace();}
	}

	public static void doAud(Path path) { doAud(path.toFile());}


	/*public static void selectPlayer(String filepath) {
		int extSep = filepath.lastIndexOf('.');
		if (extSep == -1) { extSep = 0;}
		String ext = filepath.substring(extSep).toLowerCase();
		switch (ext) {
			case "wav":
			case "au":
			case "aiff":
				return MicroAud::playBasic;// how do? i basically want function pointers...at least i think thats how i want this to work...
			case "mp3":
				return;
			case "ogg":
				return;
			default:
				System.out.println("Unsupported media type: " + ext);
		}
	}*/

	public static void main(String[] args) {
		ArgManager argManager = new ArgManager(args);
		CLIArguments arguments = argManager.getArguments();

		if (arguments.isHelpEnabled()) {
			argManager.getJCommander().usage();
			return;
		}

		argManager.init();

		//TODO: see if its feasible to provide a means of converting a wav file to a (roughly) equivalent midi file

		Vector<File> filesToPlay = new Vector<>(arguments.getAudioFiles());

		if (arguments.isPlaybackDisabled()) {loggo.info("Playback Disabled");}
		//TODO: Consider adding support for shuffling the queue with Collections.shuffle
		printPlayQueue(filesToPlay);
		if (!arguments.isPlaybackDisabled()) {
			filesToPlay.forEach(MicroAud::doAud);
		}
	}
}
//todo: -v -v -v for levels of verbosity in both debugging and announcements?
//todo: honor NO_COLOR environment variable. System.getenv("NO_COLOR")      in log4j2.properties, use ${env:NO_COLOR}