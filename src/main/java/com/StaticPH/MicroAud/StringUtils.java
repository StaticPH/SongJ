package com.StaticPH.MicroAud;

import java.util.StringJoiner;

/*
 Abstract classes define default implementations, which can be overridden by classes that extend them
 Abstract methods within non-abstract classes can also be overridden when those classes are extended
 A class can only ever extend a single abstract class
*/
@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public class StringUtils {
	//Not intended for use as an Object

	// Question: Will this just make all non-static methods in this class unusable? It seems to like causing NPEs
	//  And is there even a point in having this in an abstract class?
	private StringUtils() {}//No instantiation for you!

	// Question: does this NEED the override if it's non-static?
//  What if I make it have a static definition in the interface?
//  Can such static methods use @Override at all?
	public static String charToString(char c) { return String.valueOf(c); }

//	public String charToString(char c) { return String.valueOf(c); }

	/**
	 * @return True if <tt>String s</tt> is either <tt>null</tt> or empty
	 */
	public static boolean isNullOrEmpty(String s) { return s == null || s.isEmpty();}//|| "".equals(s)

	/**
	 * @return True if <tt>String s</tt> is either <tt>null</tt> or consists of only whitespace
	 */
	public static boolean isNullOrBlank(String s) { return s == null || s.trim().isEmpty(); }


	/**
	 * @return a new <tt>String</tt> of length <tt>n</tt> filled with character <tt>c</tt>
	 */
	public static String charNTimes(char c, int n) { return new String(new char[n]).replace('\0', c); }

	/*
	Question: Is it generally safe to make static any method that meets all the following criteria:
		takes 1 or more objects as a parameter
		uses those objects to determine a result and/or create a new object(s)
		returns [one of] the new object(s)
		neither uses nor modifies ANY non-static class member variables(AKA fields), directly or by calling another method.
	 */
	//Question: Is this variadic `strings` variable functionally treated the same as a String[]?
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
		return delimitStrings(sep, false, null, null, strings);
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
		return delimitStrings(charToString(sep), false, null, null, strings);
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * optionally with a prefix and suffix,
	 * with each element separated from its neighbor by the <tt>String</tt> <tt>sep</tt>
	 *
	 * @param sep     A <tt>String</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param enclose When true, use <tt>prefix</tt> and <tt>suffix</tt>. Ignore them if false.
	 * @param prefix  A <tt>String</tt> to prefix the joined string with
	 * @param suffix  A <tt>String</tt> to append to the joined string
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(String sep, boolean enclose, String prefix, String suffix, String... strings) {
		if (!enclose) { return joinUp(new StringJoiner(sep), strings); }
		else { return joinUp(new StringJoiner(sep, prefix, suffix), strings); }
	}

	/**
	 * Joins every element in parameter <tt>strings</tt> into a single <tt>String</tt>,
	 * optionally with a prefix and suffix,
	 * with each element separated from its neighbor by the character <tt>sep</tt>
	 *
	 * @param sep     A <tt>char</tt> to insert between each <tt>String</tt> in <tt>strings</tt>
	 * @param enclose When true, use <tt>prefix</tt> and <tt>suffix</tt>. Ignore them if false.
	 * @param prefix  A <tt>String</tt> to prefix the joined string with
	 * @param suffix  A <tt>String</tt> to append to the joined string
	 * @param strings One or more <tt>String</tt>s to join together
	 * @return A single <tt>String</tt> composed of the joined <tt>String</tt>s
	 */
	public static String delimitStrings(char sep, boolean enclose, String prefix, String suffix, String... strings) {
		return delimitStrings(charToString(sep), enclose, prefix, suffix, strings);
	}
}
