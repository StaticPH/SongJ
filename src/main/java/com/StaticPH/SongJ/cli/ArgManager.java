package com.StaticPH.SongJ.cli;

import com.StaticPH.SongJ.Constants.Colors;
import com.StaticPH.SongJ.StringUtils;
import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@SuppressWarnings({"UnnecessaryReturnStatement", "RedundantSuppression", "unused"})
public class ArgManager {
	private static Logger loggo = LogManager.getLogger("ArgManager");
	private CLIArguments arguments;
	private JCommander jCommander;
	private List<String> unknownOptions;
	private String programDesc = null;

	public CLIArguments getArguments() { return this.arguments;}

	public JCommander getJCommander() { return this.jCommander;}

	public ArgManager(String[] args) {
		this.arguments = new CLIArguments();
		this.initArgs(args);
	}

	private void initArgs(final String[] args) {
		// ???: Is there really any reason for this, if initArgs is only ever called after instantiating ArgManager.arguments?
		this.arguments = new CLIArguments();
		this.jCommander = new JCommander(this.arguments, null, args);
		this.unknownOptions = this.jCommander.getUnknownOptions();
	}

	public void setProgramDesc(String programDesc) { this.programDesc = programDesc; }

	/**
	 * Execute base behavior for ArgManager.
	 *
	 * @return {@code false} if this function results in a call to {@code JCommander.usage()}, otherwise {@code true}
	 */
	private boolean baseBehavior() {
		Vector<File> files = this.arguments.getAudioFiles();
		int fileCount = 0;
		if (files != null) {
			files.removeIf(f -> f.getName().toLowerCase().endsWith(".properties"));
			fileCount = files.size();
		}

		// Directory traversal and file list cleanup is handled during the first call to arguments.getAudioFiles();
		/* If no files were given, display the program help and ignore further arguments.*/
		if (fileCount == 0) {
			System.out.println(Colors.FG.RED + "ERROR: No playable files were discovered." + Colors.DEFAULT);
			this.jCommander.usage();
			return false;
		}
		else if (this.unknownOptions.size() != 0) {
			System.out.println(
				Colors.FG.RED + "ERROR: Unknown option(s) found: " +
				StringUtils.delimitStrings(", ", this.unknownOptions.toString()) + Colors.DEFAULT
			);
//			this.jCommander.usage();
//			return false;
		}
		return true;
	}

	@SuppressWarnings("WeakerAccess")
	protected void configureJCommander() {
		this.jCommander.setExpandAtSign(false);
		this.jCommander.setAcceptUnknownOptions(false);
		this.jCommander.setUsageFormatter(
			new CustomUsageFormatter(this.jCommander, this.programDesc, CLIArguments.realAudioFilesParamDescription)
		);
	}

	// Only ever run this AFTER instantiating ArgManager!!
	public boolean init() {
		new Colors(this.arguments.useColor());
		this.configureJCommander();
		return this.baseBehavior();
	}
}
