package com.StaticPH.SongJ;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public final class AssortedUtils {
	private static Logger loggo = LogManager.getLogger("AssortedUtils");

	private AssortedUtils() {}//no instantiating

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

	/**
	 * Try to initialize a class from its fully qualified name.
	 *
	 * @param qualName The fully qualified name of the class
	 */
	public static void tryInitClass(String qualName) {
		try { Class.forName(qualName, true, ClassLoader.getSystemClassLoader()); }
		catch (ClassNotFoundException e) {
			loggo.warn(
				"Unable to locate \"{}\".\n\tCheck that the binary name for this class is correct." +
				"\n\tFailure to locate the class may cause problems.", qualName
			);
		}
	}

	public static void printn(Object... objs) {
		for (Object obj : objs) {
			System.out.print(obj);
		}
	}

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
	//TODO: See if this can be made to work
//	public static <T> T[] getFilledArr(int len, T val){
//		final T[] arr = T[len];
//		Arrays.fill(arr, val);
//		return arr;
//	}

	/** Wrapper around a Thread.sleep() call just to hide away the exception handling. */
	public static void doze(long timeoutSec) {
		try { Thread.sleep(timeoutSec);}
		catch (InterruptedException e) { e.printStackTrace();}
	}
}
