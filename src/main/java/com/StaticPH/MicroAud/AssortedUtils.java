package com.StaticPH.MicroAud;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public final class AssortedUtils {
	// Currently this project is small enough that I don't think it worth having context-specific loggers; just use the global one
	private static Logger loggo = Logger.getGlobal();       // NOTE: I WANT TO KEEP THIS

	// I need to either allow instantiation, make all the methods static, or not make the class final so that I can subclass it
//	private AssortedUtils() {}//no instantiating

	//???: Should I not assign loggo with its declaration, but instead add a check in getLogger
	//      ```     if (!loggo){loggo = Logger.getGlobal();}     ```
	public static Logger getLogger() { return loggo; }
	//TODO: public, ?ideally non-static?, method that sets filter level for loggo

	public static void logArgs(String[] args) {
		//These two statements could be combined, eliminating a variable, but at the cost of readability
		String argList = StringUtils.delimitStrings(",", true, "args: [", "]", args);
		loggo.info(argList);
	}

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
