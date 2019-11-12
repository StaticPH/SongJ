package com.StaticPH.MicroAud;

import com.sun.istack.internal.NotNull;

/*
	Defines what methods must look like;
	i.e., return type, method name, static/not, how many parameters it accepts, types of each parameter

	A single class can implement an unlimited number of interfaces
*/
@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public interface IStringUtils {

	//	String charToString(char c);
	static String charToString(char c) {return String.valueOf(c);}
//	default String charToString(char c){return String.valueOf(c);}

	static boolean isNullOrEmpty(String s){ return s == null || s.isEmpty();}

	/*
	Question: Why not/use 'default' functions?
	 Can a default method be overridden by an implementing class?
	 Because if so, that would make default methods effectively abstract methods, but because they're declared in an interface,
	      default methods from multiple different interfaces could be overridden by the same inheriting(implementing) class.
	 Additionally, it sounds like that would allow me to call non-static methods using an uninitialized static variable of the class type.
	 It seems that static and default are mutually exclusive, a static cannot override a non-static default,
	      and a default implementation cannot be called from a static context
	 ~~~
		default StringJoiner delimitStrings(@NotNull String sep, @NotNull String... strings) {
			return delimitStrings(sep, false, null, null, strings);
		}
	*/
	String delimitStrings(@NotNull String sep, String... strings);

	String delimitStrings(@NotNull char sep, String... strings);

	String delimitStrings(
		@NotNull String sep, boolean enclose, @NotNull String prefix,
		@NotNull String suffix, String... strings
	);

	String delimitStrings(
		@NotNull char sep, boolean enclose, @NotNull String prefix,
		@NotNull String suffix, String... strings
	);


}