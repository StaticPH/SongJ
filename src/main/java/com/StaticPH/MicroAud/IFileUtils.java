package com.StaticPH.MicroAud;

import java.io.File;

@SuppressWarnings("unused")
public interface IFileUtils {

//	static  <T> T zero(Class<T> type) { return (T) type; }


	File getFileFromScanner ();
//	static File getFileFromScanner() {return null;}

	File getFileFromPath(String path);
}
