package com.StaticPH.MicroAud;

import java.util.StringJoiner;

/*
 Abstract classes define default implementations, which can be overridden by classes that extend them
 Abstract methods within non-abstract classes can also be overridden when those classes are extended
 A class can only ever extend a single abstract class
*/
@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public abstract class AbstractStringUtils implements IStringUtils {
	//Not intended for use as an Object

	// Question: Will this just make all non-static methods in this class unusable? It seems to like causing NPEs
	//  And is there even a point in having this in an abstract class?
//	private StringUtils(){}//No instantiation for you!

// Question: does this NEED the override if it's non-static?
//  What if I make it have a static definition in the interface?
//  Can such static methods use @Override at all?
//	@Override
	public static String charToString(char c) { return String.valueOf(c); }

//	@Override       // This works if I declare it as a default in the interface as opposed to a static
//	public String charToString(char c) { return String.valueOf(c); }

	static boolean isNullOrEmpty(String s){ return s == null || s.isEmpty();}//|| "".equals(s)

	/*
	Question: Is it generally safe to make static any method that meets all the following criteria:
		takes 1 or more objects as a parameter
		uses those objects to determine a result and/or create a new object(s)
		returns [one of] the new object(s)
		neither uses nor modifies ANY non-static class member variables(AKA fields), directly or by calling another method.
	 */
	/*
	 Question: I dont know if I want my functions here to be static, but I DO want them to be overridable;
            can I do that AND be able to call them from within a static context WITHOUT having to create a static instance variable
	          import com.StaticPH.MicroAud.StringUtils;
	          public static void main(String[] args){
	              String s = StringUtils.charToString('s');
	              ... // Stuff
	          }
	      instead of:
	          private static StringUtils sUtil;
	          public static void main(String[] args){
	              String s = sUtil.charToString('s');
	              ... // Stuff
	          }
	*/
	//Question: Is this variadic `strings` variable functionally treated the same as a String[]?
	private String joinUp(StringJoiner joiner, String... strings) {
		for (String s : strings) {
			joiner.add(s);
		}
		return joiner.toString();
	}

	//Question: Should this be annotated with @Override, or is it just fine without?
	public String delimitStrings(String sep, String... strings) {
		return delimitStrings(sep, false, null, null, strings);
	}

	@Override   //Question: Is the @Override required here? What happens if I remove it?
	public String delimitStrings(char sep, String... strings) {
		return delimitStrings(charToString(sep), false, null, null, strings);
	}

	public String delimitStrings(String sep, boolean enclose, String prefix, String suffix, String... strings) {
		if (!enclose) { return joinUp(new StringJoiner(sep), strings); }
		else { return joinUp(new StringJoiner(sep, prefix, suffix), strings); }
	}

	@Override
	public String delimitStrings(char sep, boolean enclose, String prefix, String suffix, String... strings) {
		return delimitStrings(charToString(sep), enclose, prefix, suffix, strings);
	}
}
