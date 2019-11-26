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
	private StringUtils(){}//No instantiation for you!

// Question: does this NEED the override if it's non-static?
//  What if I make it have a static definition in the interface?
//  Can such static methods use @Override at all?
	public static String charToString(char c) { return String.valueOf(c); }

//	public String charToString(char c) { return String.valueOf(c); }

	public static boolean isNullOrEmpty(String s){ return s == null || s.isEmpty();}//|| "".equals(s)
	public static boolean isNullOrBlank(String s){ return s == null || s.trim().isEmpty();} //Formerly isNullOrKindaEmpty

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

	public static String delimitStrings(String sep, String... strings) {
		return delimitStrings(sep, false, null, null, strings);
	}

	public static String delimitStrings(char sep, String... strings) {
		return delimitStrings(charToString(sep), false, null, null, strings);
	}

	public static String delimitStrings(String sep, boolean enclose, String prefix, String suffix, String... strings) {
		if (!enclose) { return joinUp(new StringJoiner(sep), strings); }
		else { return joinUp(new StringJoiner(sep, prefix, suffix), strings); }
	}

	public static String delimitStrings(char sep, boolean enclose, String prefix, String suffix, String... strings) {
		return delimitStrings(charToString(sep), enclose, prefix, suffix, strings);
	}
}
