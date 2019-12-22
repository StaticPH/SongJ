package com.StaticPH.MicroAud.cli;

import com.beust.jcommander.IUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.Strings;
import com.beust.jcommander.WrappedParameter;
import com.beust.jcommander.internal.Lists;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static com.beust.jcommander.DefaultUsageFormatter.getI18nString;
import static com.beust.jcommander.DefaultUsageFormatter.s;

/**
 * This file is mostly copied from {@link com.beust.jcommander.DefaultUsageFormatter},
 * but with some formatting tweaks, single-character strings swapped for chars, rearrangement of some append calls.
 * The biggest real difference, aside from having multiple constructors, is that this version also prints
 * a program description and the description of the main parameter, if they are present.
 */
@SuppressWarnings({"unused", "OverlyLongMethod", "WeakerAccess", "RedundantSuppression"})
public class CustomUsageFormatter implements IUsageFormatter {
	private final JCommander commander;
	private final String mainParamDesc;
	private final String programDesc;

	public CustomUsageFormatter(JCommander commander) {
		this.commander = commander;
		this.mainParamDesc = null;
		this.programDesc = null;
	}

	public CustomUsageFormatter(JCommander commander, String mainParamDesc) {
		this.commander = commander;
		this.mainParamDesc = mainParamDesc;
		this.programDesc = null;
	}

	public CustomUsageFormatter(JCommander commander, String programDesc, String mainParamDesc) {
		this.commander = commander;
		this.mainParamDesc = mainParamDesc;
		this.programDesc = programDesc;
	}

	/**
	 * Prints the usage to {@link JCommander#getConsole()} on the underlying commander instance.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#usage(String)}</p>
	 */
	@Override
	public final void usage(String commandName) {
		StringBuilder sb = new StringBuilder();
		this.usage(commandName, sb);
		this.commander.getConsole().println(sb.toString());
	}

	/**
	 * Store the usage for the argument command in the argument string builder.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#usage(String, StringBuilder)} </p>
	 */
	@Override
	public final void usage(String commandName, StringBuilder out) { this.usage(commandName, out, "");}

	/**
	 * Store the usage in the argument string builder.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#usage(StringBuilder)} </p>
	 */
	@Override
	public final void usage(StringBuilder out) { this.usage(out, "");}

	/**
	 * Store the usage for the command in the argument string builder,
	 * indenting every line with the value of indent.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#usage(String, StringBuilder, String)} </p>
	 */
	@Override
	public final void usage(String commandName, StringBuilder out, String indent) {
		String description = this.getCommandDescription(commandName);
		JCommander jc = this.commander.findCommandByAlias(commandName);

		if (description != null) { out.append(indent).append(description).append('\n'); }
		jc.getUsageFormatter().usage(out, indent);
	}

	/**
	 * Stores the usage in the argument string builder, with the argument indentation. This works by appending
	 * each portion of the help in the following order. Their outputs can be modified by overriding them in a
	 * subclass of this class.
	 * <p>Largely copied from {@link com.beust.jcommander.DefaultUsageFormatter#usage(StringBuilder, String)}</p>
	 */
	@Override
	public void usage(StringBuilder out, String indent) {
		if (this.commander.getDescriptions() == null) { this.commander.createDescriptions(); }
		boolean hasCommands = !this.commander.getCommands().isEmpty();
		boolean hasOptions = !this.commander.getDescriptions().isEmpty();

		// Indentation constants
		final int descriptionIndent = 6;
		final int indentCount = indent.length() + descriptionIndent;

		// Append first line (aka main line) of the usage
		this.appendMainLine(out, hasOptions, hasCommands, indentCount, indent);

		// Append the description of the program
		if (this.programDesc != null){ out.append("  ").append(this.programDesc).append('\n'); }

		// Append the actual description of the main parameter
		if (
			this.commander.getMainParameter() != null &&
			this.commander.getMainParameterValue() != null &&
			this.mainParamDesc != null
		) {
			this.wrapDescription(out, indentCount-4, s(indentCount-4) + this.mainParamDesc);
			out.append("\n\n");
		}
		// Align the descriptions at the "longestName" column
		int longestName = 0;
		List<ParameterDescription> sortedParameters = Lists.newArrayList();

		for (ParameterDescription pd : this.commander.getFields().values()) {
			if (!pd.getParameter().hidden()) {
				sortedParameters.add(pd);
				// + to have an extra space between the name and the description
				int length = pd.getNames().length() + 2;

				if (length > longestName) { longestName = length; }
			}
		}

		// Sort the options
		sortedParameters.sort(this.commander.getParameterDescriptionComparator());

		// Append all the parameter names and descriptions
		this.appendAllParametersDetails(out, indentCount, indent, sortedParameters);

		// Append commands if they were specified
		if (hasCommands) { this.appendCommands(out, indentCount, descriptionIndent, indent); }
	}

	/**
	 * Appends the main line segment of the usage to the argument string builder, indenting every
	 * line with indentCount-many indent.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#appendMainLine(StringBuilder, boolean, boolean, int, String)}</p>
	 *
	 * @param out         the builder to append to
	 * @param hasOptions  if the options section should be appended
	 * @param hasCommands if the comments section should be appended
	 * @param indentCount the amount of indentation to apply
	 * @param indent      the indentation
	 */
	public void appendMainLine(
		StringBuilder out, boolean hasOptions, boolean hasCommands, int indentCount, String indent
	) {
		String programName = this.commander.getProgramDisplayName() != null
		                     ? this.commander.getProgramDisplayName()
		                     : "<main class>";
		StringBuilder mainLine = new StringBuilder();
		mainLine.append(indent).append("Usage: ").append(programName);

		if (hasOptions) { mainLine.append(" [options]"); }

		if (hasCommands) { mainLine.append(indent).append(" [command] [command options]"); }

		if (this.commander.getMainParameter() != null && this.commander.getMainParameterValue() != null) {
			mainLine.append(' ').append(this.commander.getMainParameterValue().getDescription());
		}
		this.wrapDescription(out, indentCount, mainLine.toString());
		out.append('\n');
	}

	/**
	 * Appends the details of all parameters in the given order to the argument string builder, indenting every
	 * line with indentCount-many indent.
	 * <p>Largely copied from
	 * {@link com.beust.jcommander.DefaultUsageFormatter#appendAllParametersDetails(StringBuilder, int, String, List)}</p>
	 *
	 * @param out              the builder to append to
	 * @param indentCount      the amount of indentation to apply
	 * @param indent           the indentation
	 * @param sortedParameters the parameters to append to the builder
	 */
	public void appendAllParametersDetails(
		StringBuilder out, int indentCount, String indent,
		List<? extends ParameterDescription> sortedParameters
	) {
		if (sortedParameters.size() > 0) { out.append(indent).append("  Options:\n"); }

		for (ParameterDescription pd : sortedParameters) {
			WrappedParameter parameter = pd.getParameter();
			String description = pd.getDescription();
			boolean hasDescription = !description.isEmpty();

			// First line, command name
			out.append(indent)
			   .append("  ")
			   .append(parameter.required() ? "* " : "  ")
			   .append(pd.getNames())
			   .append('\n');

			if (hasDescription) { this.wrapDescription(out, indentCount, s(indentCount) + description); }
			Object def = pd.getDefault();

			if (pd.isDynamicParameter()) {
				String syntax = "Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value";

				if (hasDescription) { out.append('\n'); }
				out.append(s(indentCount)).append(syntax);
			}

			if (def != null && !pd.isHelp()) {
				String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
				String defaultText = "Default: " + (parameter.password() ? "********" : displayedDef);

				if (hasDescription) { out.append('\n'); }
				out.append(s(indentCount)).append(defaultText);
			}
			Class<?> type = pd.getParameterized().getType();

			if (type.isEnum()) {
				String valueList = EnumSet.allOf((Class<? extends Enum>) type).toString();
				String possibleValues = "Possible Values: " + valueList;

				// Prevent duplicate values list, since it is set as 'Options: [values]' if the description
				// of an enum field is empty in ParameterDescription#init(..)
				if (!description.contains("Options: " + valueList)) {
					if (hasDescription) { out.append('\n'); }
					out.append(s(indentCount)).append(possibleValues);
				}
			}
			out.append('\n');
		}
	}

	/**
	 * Appends the details of all commands to the argument string builder, indenting every line with
	 * indentCount-many indent. The commands are obtained from calling
	 * {@link JCommander#getRawCommands()} and the commands are resolved using
	 * {@link JCommander#findCommandByAlias(String)} on the underlying commander instance.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#appendCommands(StringBuilder, int, int, String)}</p>
	 *
	 * @param out               the builder to append to
	 * @param indentCount       the amount of indentation to apply
	 * @param descriptionIndent the indentation for the description
	 * @param indent            the indentation
	 */
	public void appendCommands(StringBuilder out, int indentCount, int descriptionIndent, String indent) {
		out.append(indent).append("  Commands:\n");

		// The magic value 3 is the number of spaces between the name of the option and its description
		for (Map.Entry<JCommander.ProgramName, JCommander> commands : this.commander.getRawCommands().entrySet()) {
			Object arg = commands.getValue().getObjects().get(0);
			Parameters p = arg.getClass().getAnnotation(Parameters.class);

			if (p == null || !p.hidden()) {
				JCommander.ProgramName progName = commands.getKey();
				String dispName = progName.getDisplayName();
				String description = indent + s(4) + dispName + s(6) + this.getCommandDescription(progName.getName());
				this.wrapDescription(out, indentCount + descriptionIndent, description);
				out.append('\n');

				// Options for this command
				JCommander jc = this.commander.findCommandByAlias(progName.getName());
				jc.getUsageFormatter().usage(out, indent + s(6));
				out.append('\n');
			}
		}
	}

	/**
	 * Wrap a potentially long line to the value obtained by calling {@link JCommander#getColumnSize()} on the
	 * underlying commander instance.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#wrapDescription(StringBuilder, int, int, String) }</p>
	 *
	 * @param out               the output
	 * @param indent            the indentation in spaces for lines after the first line.
	 * @param currentLineIndent the length of the indentation of the initial line
	 * @param description       the text to wrap. No extra spaces are inserted before {@code
	 *                          description}. If the first line needs to be indented prepend the
	 *                          correct number of spaces to {@code description}.
	 */
	public void wrapDescription(StringBuilder out, int indent, int currentLineIndent, String description) {
		int max = this.commander.getColumnSize();
		String[] words = description.split(" ");
		int current = currentLineIndent;

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			if (word.length() > max || current + 1 + word.length() <= max) {
				out.append(word);
				current += word.length();

				if (i != words.length - 1) {
					out.append(' ');
					current++;
				}
			}
			else {
				out.append('\n').append(s(indent)).append(word).append(' ');
				current = indent + word.length() + 1;
			}
		}
	}

	/**
	 * Wrap a potentially long line to { @link #commander#getColumnSize()}.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#wrapDescription(StringBuilder, int, String)}</p>
	 *
	 * @param out         the output
	 * @param indent      the indentation in spaces for lines after the first line.
	 * @param description the text to wrap. No extra spaces are inserted before {@code
	 *                    description}. If the first line needs to be indented prepend the
	 *                    correct number of spaces to {@code description}.
	 * @see #wrapDescription(StringBuilder, int, int, String)
	 */
	public void wrapDescription(StringBuilder out, int indent, String description) {
		this.wrapDescription(out, indent, 0, description);
	}

	/**
	 * Returns the description of the command corresponding to the argument command name. The commands are resolved
	 * by calling {@link JCommander#findCommandByAlias(String)}, and the default resource bundle used from
	 * {@link JCommander#getBundle()} on the underlying commander instance.
	 * <p>Copied from {@link com.beust.jcommander.DefaultUsageFormatter#getCommandDescription(String)}</p>
	 *
	 * @param commandName the name of the command to get the description for
	 * @return the description of the command.
	 */
	public String getCommandDescription(String commandName) {
		JCommander jc = this.commander.findCommandByAlias(commandName);

		if (jc == null) { throw new ParameterException("Asking description for unknown command: " + commandName); }
		Object arg = jc.getObjects().get(0);
		Parameters p = arg.getClass().getAnnotation(Parameters.class);
		java.util.ResourceBundle bundle;
		String result = null;

		if (p != null) {
			result = p.commandDescription();
			String bundleName = p.resourceBundle();

			if (!bundleName.isEmpty()) { bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault()); }
			else { bundle = this.commander.getBundle(); }

			if (bundle != null) {
				String descriptionKey = p.commandDescriptionKey();
				if (!descriptionKey.isEmpty()) {
					result = getI18nString(bundle, descriptionKey, p.commandDescription());
				}
			}
		}
		return result;
	}

}
