package com.StaticPH.MicroAud;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.StaticPH.MicroAud.AbstractStringUtils.charToString;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
final class AssortedUtils implements IFileUtils {
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
		getLogger().info(argList);
	}

	static void printn(Object... objs) {
		for (Object obj : objs) {
			System.out.print(obj);
		}
	}

	public File getFileFromScanner() {
		//Question: realistically could probably make this static in the interface, but should I?
		File f;

		try (Scanner userInput = new Scanner(System.in)) {
			f = new File(userInput.next()).getCanonicalFile();
//			f = f.getCanonicalFile();
			loggo.info(
//				String.format("'%s' resolved to canonical path: '%s'\n", f.getPath(), f.getCanonicalPath()) // String.format performs poorly
				'\'' + f.getPath() + "' resolved to canonical path: '" + f.getCanonicalPath() + "'\n"
			);
			if (!f.exists()) {
				loggo.info("File not found: '" + f.getCanonicalPath() + "'\n");
				f = null;
			}
		}
		catch (IOException e) {
			loggo.log(Level.FINER, "Error constructing canonical pathname", e);
			f = null;
		}
		return f;
	}

	public File getFileFromPath(String path) {  //TODO: REVAMP ME
		File f = new File(path);
		if (!f.exists()) {
			System.out.println("File not found");
			f = null;
		}
		return f;
	}   //this should probably throw exceptions, not catch them


}

@SuppressWarnings("unused")
class MathUtils {
	private MathUtils() {}

	public static boolean isOdd(int n) {return (n % 2) != 0;}

	public static boolean isOdd(long n) {return (n % 2) != 0;}

	public static boolean isEven(int n) {return (n % 2) == 0;}

	public static boolean isEven(long n) {return (n % 2) == 0;}

	public static int foo(int a) {return a;}
	//	double nthRoot (const double rootOf, const int nth) {return Math.pow (rootOf, 1.0 / nth);}
}

final class StringUtils extends AbstractStringUtils {
	//Change nothing from the abstract class, but allow instantiation in order to access non-static methods
	StringUtils() {}
}