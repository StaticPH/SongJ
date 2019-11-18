package com.StaticPH.MicroAud;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.StaticPH.MicroAud.AbstractStringUtils.charToString;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
final class AssortedUtils {
	// Currently this project is small enough that I don't think it worth having context-specific loggers; just use the global one
	private static Logger loggo = Logger.getGlobal();       // NOTE: I WANT TO KEEP THIS
	private static StringUtils su = new StringUtils();      //FIXME: do not want

	// I need to either allow instantiation, make all the methods static, or not make the class final so that I can subclass it
//	private AssortedUtils() {}//no instantiating

	//???: Should I not assign loggo with its declaration, but instead add a check in getLogger
	//      ```     if (!loggo){loggo = Logger.getGlobal();}     ```
	public static Logger getLogger() { return loggo; }
	//TODO: public, ?ideally non-static?, method that sets filter level for loggo

	public static void logArgs(String[] args) {
		// FIXME:   I REALLY WANT THIS TO BE `StringUtils.delimitStrings(` !!!
		// ???: Do I accomplish this by making delimitStrings static?
		//      Do I leave it as is?
		//      Do I remove the private static sUtil variable from the class scope,
		//          and instead create a method-scoped instance variable of StringUtils every time I call this method?

		//These two statements could be combined, eliminating a variable, but at the cost of readability
		String argList = su.delimitStrings(charToString(','), true, "args: [", charToString(']'), args);
		loggo.info(argList);
	}

	static void printn(Object... objs) {
		for (Object obj : objs) {
			System.out.print(obj);
		}
	}

/*	public static void loopClip(Clip c){
		if (clip.isRunning())
			clip.stop();   // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
		clip.start();     // Start playing
	}*/

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
	 * This is equivalent to <code>filter(iterable, comparer.negate())</code> where <code> comparer != null</code>
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
//		boolean didModify = false;
//		if (iterable != null && comparer != null) {
//			for (Iterator<T> iter = iterable.iterator(); iter.hasNext(); ) {
//				if (comparer.test(iter.next())) {
//					iter.remove();
//					didModify = true;
//				}
//			}
//		}
//		return didModify;
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
}

@SuppressWarnings("unused")
class MathUtils {
	private MathUtils() {}

	public static boolean isOdd(int n) {return (n % 2) != 0;}

	public static boolean isOdd(long n) {return (n % 2) != 0;}

	public static boolean isEven(int n) {return (n % 2) == 0;}

	public static boolean isEven(long n) {return (n % 2) == 0;}

	//	double nthRoot (const double rootOf, const int nth) {return Math.pow (rootOf, 1.0 / nth);}
}

final class StringUtils extends AbstractStringUtils {
	//Change nothing from the abstract class, but allow instantiation in order to access non-static methods
	StringUtils() {}
}