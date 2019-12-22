package com.StaticPH.MicroAud;

import com.StaticPH.MicroAud.audioPlayer.AbstractAudioPlayer;
import com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers;
import com.StaticPH.MicroAud.audioPlayer.PlayerTypeMap;
import com.StaticPH.MicroAud.cli.ArgManager;
import com.StaticPH.MicroAud.cli.CLIArguments;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Vector;

import static com.StaticPH.MicroAud.StringUtils.delimitStrings;
import static com.StaticPH.MicroAud.audioPlayer.PlaybackHelpers.*;


@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class MicroAud {
	// ======= Class (Static) Variables ======= //
	private static final Logger loggo = AssortedUtils.getLogger("MicroAud");

	// ======= Instance (Non-Static) Variables ======= //

	// ======= Class Constructors ======= //
	public MicroAud() {}

	// ======= Methods ======= //

	public static void logArgs(String[] args) { loggo.info(delimitStrings(",", "args: [", "]", args));}

	public static void preloadPlayers() throws FileNotFoundException {
		PropLoader loader = new PropLoader();
		loader.loadPropResourceFile("playerHandlers.properties");
		Properties props = loader.getProps();
		props.list(System.out);
		System.out.println();
		props.forEach((k, v) -> {
			if (!(StringUtils.isNullOrBlank((String) k) || StringUtils.isNullOrBlank((String) v))) {
				AssortedUtils.tryInitClass(v.toString());
			}
		});
	}

	public static void doAud(final File file) {
		String path = file.getPath();
		String contentType = FileUtils.magicTest(file);
		AbstractAudioPlayer A;
		try {
			A = PlayerTypeMap.getPlayerWithMappingTo(contentType.toUpperCase());
		}
		catch (UnsupportedAudioFileException e) {
			System.err.println("Unable to play \"" + file.getName() + "\": " + e.getMessage());
			loggo.debug(() -> getUnsupportedAudioFileMessage(file.getName()) +
			                  "\nFile's content-type has been evaluated as: " + contentType);
			return;
		}
		try {
			printNowPlaying(A, file);
//				A.setShow_ms(true);
			A.playFile(file);
			System.out.println("\033[1;32mFinished playing from " + file.getPath() + "\033[0m");
			printBar();
		}
		catch (UnsupportedAudioFileException e) {
			loggo.warn(e.fillInStackTrace());
			System.err.println("Skipping unsupported file (" + file.getName() + ')');
		}
	}

	public static void doAud(Path path) { doAud(path.toFile());}

	public static void processFiles(CLIArguments arguments) throws FileNotFoundException {
		Vector<File> filesToPlay = new Vector<>(arguments.getAudioFiles());

		if (arguments.isPlaybackDisabled()) {loggo.info("Playback Disabled");}

		//TODO: Consider adding support for shuffling the queue with Collections.shuffle
		PlaybackHelpers.printPlayQueue(filesToPlay);

		if (!arguments.isPlaybackDisabled()) {
			preloadPlayers();
			filesToPlay.forEach(MicroAud::doAud);
		}
	}

	public static void main(String[] args) {
		ArgManager argManager = new ArgManager(args);
		argManager.setProgramDesc("TODO: PROPER DESCRIPTION"); //TODO: Proper description

		// If an issue with the arguments has already resulted in a call to the JCommander.usage() method, exit
		if (!argManager.init()) { System.exit(1); }

		CLIArguments arguments = argManager.getArguments();

		if (arguments.isHelpEnabled()) {
			argManager.getJCommander().usage();
			System.exit(0);
		}

		try {
			processFiles(arguments);
		}
		catch (FileNotFoundException e) {
			loggo.fatal(e.fillInStackTrace());
			System.err.println("Aborting.");
			System.exit(1);
		}
	}
}
//todo: honor NO_COLOR environment variable. System.getenv("NO_COLOR")      in log4j2.properties, use ${env:NO_COLOR}
