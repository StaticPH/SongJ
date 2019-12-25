package com.StaticPH.SongJ;

import java.util.Objects;
import java.util.StringJoiner;

/*
 Abstract classes define default implementations, which can be overridden by classes that extend them
 Abstract methods within non-abstract classes can also be overridden when those classes are extended
 A class can only ever extend a single abstract class
*/
@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public class StringUtils {
	private StringUtils() {}//No instantiation for you!

	/** @return The <tt>String</tt> value of <tt>char c</tt> */
	public static String charToString(char c) { return String.valueOf(c); }

	/** @return True if <tt>String s</tt> is either <tt>null</tt> or empty */
	public static boolean isNullOrEmpty(String s) { return s == null || s.isEmpty();}//|| "".equals(s)

	/** @return True if <tt>String s</tt> is either <tt>null</tt> or consists of only whitespace */
	public static boolean isNullOrBlank(String s) { return s == null || s.trim().isEmpty(); }

	/** @return A new <tt>String</tt> of length <tt>n</tt> filled with character <tt>c</tt> */
	public static String charNTimes(char c, int n) { return new String(new char[n]).replace('\0', c); }

	private static String joinUp(StringJoiner joiner, String... strings) {
		for (String s : strings) {
			joiner.add(s);
		}
		return joiner.toString();
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * with each element separated from its neighbor by the <tt>String</tt> <tt>sep</tt>
	 *
	 * @param sep     A <tt>String</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(String sep, String... strings) {
		return delimitStrings(sep, "", "", strings);
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * with each element separated from its neighbor by the character <tt>sep</tt>
	 *
	 * @param sep     A <tt>char</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(char sep, String... strings) {
		return delimitStrings(charToString(sep), "", "", strings);
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * optionally with a prefix and suffix,
	 * with each element separated from its neighbor by the <tt>String</tt> <tt>sep</tt>
	 *
	 * @param sep     A <tt>String</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param prefix  A <tt>String</tt> to prefix the joined string with
	 * @param suffix  A <tt>String</tt> to append to the joined string
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(String sep, String prefix, String suffix, String... strings) {
		Objects.requireNonNull(prefix);
		Objects.requireNonNull(suffix);
		return joinUp(new StringJoiner(sep, prefix, suffix), strings);
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * optionally with a prefix and suffix,
	 * with each element separated from its neighbor by the character <tt>sep</tt>
	 *
	 * @param sep     A <tt>char</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param prefix  A <tt>String</tt> to prefix the joined string with
	 * @param suffix  A <tt>String</tt> to append to the joined string
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(char sep, String prefix, String suffix, String... strings) {
		return delimitStrings(charToString(sep), prefix, suffix, strings);
	}
}
