package com.StaticPH.MicroAud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

/* can choose to forgoe all this nonsense with
https://stackoverflow.com/a/5903474
 */


@SuppressWarnings({"WeakerAccess", "unused"})
public class PropLoader {
	private final String resourceHomePath = this.findResourceHomePath();
	private final Path path = new File(this.resourceHomePath).toPath();

	private final Properties props = new Properties();


	protected PropLoader() {}

	protected String findResourceHomePath() {
		// see https://stackoverflow.com/questions/1771679/difference-between-threads-context-class-loader-and-normal-classloader
		final URL p = Thread.currentThread().getContextClassLoader().getResource("props");
		return Objects.requireNonNull(p).getPath();
	}

	public Properties getProps() { return this.props;}

	public String getResourceHomePath() { return this.resourceHomePath;}

	public void loadAllPropFiles() {
		try (Stream<Path> s = Files.list(this.path)) { s.forEach(this::_loadPropFile); }
		catch (IOException e) { e.printStackTrace(); }
	}

	/** Internal only wrapper around loadPropFile that ignores IOException */
	private void _loadPropFile(Path path) {
		try {this.loadPropFile(path);}
		catch (IOException e) { e.printStackTrace();}
	}

	/** NOTE: file path here is the absolute file path */
	public void loadPropFile(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(
				"Unable to locate file \"" + file.getPath() + '\"'
			);
		}
		try (InputStream in = new FileInputStream(file)) { this.props.load(in); }
		catch (IOException e) { e.printStackTrace();}
	}

	public void loadPropFile(Path path) throws FileNotFoundException { this.loadPropFile(path.toFile()); }

	public void loadPropFile(String filePath) throws FileNotFoundException { this.loadPropFile(new File(filePath)); }

	/** NOTE: file path here is the path relative to src/main/resources/ */
	public void loadPropResourceFile(File file) throws FileNotFoundException {
		this.loadPropFile(this.path.resolve(file.toString()).toFile());
	}

	public void loadPropResourceFile(Path path) throws FileNotFoundException {
		this.loadPropFile(this.path.resolve(path).toFile());
	}

	public void loadPropResourceFile(String filePath) throws FileNotFoundException {
		this.loadPropFile(this.path.resolve(filePath));
	}
}
