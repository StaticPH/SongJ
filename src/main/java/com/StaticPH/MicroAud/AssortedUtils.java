package com.StaticPH.MicroAud;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;
//import java.util.logging.Logger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public final class AssortedUtils {
	private static Logger loggo = LogManager.getLogger("AssortedUtils");

	// I need to either allow instantiation, make all the methods static, or not make the class final so that I can subclass it
//	private AssortedUtils() {}//no instantiating

	//???: Is it even worth doing this instead of just including the import for LogManager everywhere?
	public static Logger getLogger(String name) { return LogManager.getLogger(name); }

	public static Logger getLogger(Object obj) { return LogManager.getLogger(obj); }

	public static Logger getLogger(Class<?> clazz) { return LogManager.getLogger(clazz); }

	/** Print the path to a class, including classes from libraries */
	public static void printClasspath(Class<?> clazz) {
		System.out.println(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
	}

	/** Print the classpath for this module */
	public static void printClasspath() { System.out.println(System.getProperty("java.class.path"));}

	static void printn(Object... objs) {
		for (Object obj : objs) {
			System.out.print(obj);
		}
	}

	//TODO: decide between @NotNull on Predicate(and maybe Iterable) or returning false when either is null
	// also whether to use @NotNull or call `Objects.requireNonNull(VARIABLE);` instead

	/**
	 * Filter an Iterable by applying a Predicate function to each element within.
	 * If the predicate is false, remove the element.
	 *
	 * @param <T>      The type of object contained within the Iterable
	 * @param iterable An Iterable object collection to filter. May be null.
	 * @param comparer A Predicate function according to which the contents of iterable should be filtered.
	 *                 May be null.
	 * @return Returns true if the filtering modified the Iterable; returns false otherwise
	 * Always returns false if either Iterable or Predicate is null.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static <T> boolean filter(Iterable<T> iterable, Predicate<? super T> comparer) {
		boolean didModify = false;
		if (iterable != null && comparer != null) {
			for (Iterator<T> iter = iterable.iterator(); iter.hasNext(); ) {
				if (!comparer.test(iter.next())) {
					iter.remove();
					didModify = true;
				}
			}
		}
		return didModify;
	}

	/**
	 * Filter an Iterable by applying a Predicate function to each element within.
	 * If the predicate is true, remove the element.
	 * <p>
	 * This is equivalent to {@code filter(iterable, comparer.negate())} where {@code comparer != null}
	 * </p>
	 *
	 * @param <T>      The type of object contained within the Iterable
	 * @param iterable An Iterable object collection to filter. May be null.
	 * @param comparer A Predicate function according to which the contents of iterable should be filtered.
	 *                 May be null.
	 * @return Returns true if the filtering modified the Iterable; returns false otherwise
	 * Always returns false if either Iterable or Predicate is null.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static <T> boolean inverseFilter(Iterable<T> iterable, Predicate<? super T> comparer) {
		boolean didModify = false;
		if (iterable != null && comparer != null) {didModify = filter(iterable, comparer.negate());}
		return didModify;
	}

/*      YUNO???
	public static <T> void toss(Iterable<T> iterable){
		Predicate<?super T> p = new Predicate() {
			@Override
			public boolean test(Object o) {
				return StringUtils.isNullOrEmpty(o.toString());
			}
		};
		AssortedUtils.inverseFilter(iterable, p );
	}
*/

	/**
	 * Create a <tt>byte</tt> array full of null bytes
	 *
	 * @param length How long the <tt>byte</tt> array should be. and thus, how many null bytes will be in it.
	 * @return An array of null bytes.
	 */
	public static byte[] getNullBytes(int length) {
		final byte[] bytes = new byte[length];
		Arrays.fill(bytes, (byte) 0);
		return bytes;
	}

	public static void printBar() {
		System.out.println("\033[1;36m" + StringUtils.charNTimes('=', 48) + "\033[0m" + '\n');
	}

	/** Hide away the exception handling for a Thread.sleep() call. */
	public static void doze(long timeoutSec) {
		try { Thread.sleep(timeoutSec);}
		catch (InterruptedException e) { e.printStackTrace();}
	}
}
