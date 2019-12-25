package com.StaticPH.SongJ;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Constants {
	public final static class Colors extends ColorConstants {
		public Colors(boolean isEnabled) { super(isEnabled);}
	}

	private static class ColorConstants {
		public static String DEFAULT;

		public static class BG {
			public static String BLACK;
			public static String RED;
			public static String GREEN;
			public static String YELLOW;
			public static String BLUE;
			public static String MAGENTA;
			public static String CYAN;
			public static String GRAY;
		}

		public static class FG {
			public static String BLACK;
			public static String RED;
			public static String GREEN;
			public static String YELLOW;
			public static String BLUE;
			public static String MAGENTA;
			public static String CYAN;
			public static String GRAY;
			public static String BR_BLACK;
			public static String BR_RED;
			public static String BR_GREEN;
			public static String BR_YELLOW;
			public static String BR_BLUE;
			public static String BR_MAGENTA;
			public static String BR_CYAN;
			public static String BR_GRAY;
		}

		public ColorConstants(boolean isEnabled) {
			DEFAULT = isEnabled ? "\033[0m" : "";

			BG.BLACK = isEnabled ? "\033[0;40m" : "";
			BG.RED = isEnabled ? "\033[0;41m" : "";
			BG.GREEN = isEnabled ? "\033[0;42m" : "";
			BG.YELLOW = isEnabled ? "\033[0;43m" : "";
			BG.BLUE = isEnabled ? "\033[0;44m" : "";
			BG.MAGENTA = isEnabled ? "\033[0;45m" : "";
			BG.CYAN = isEnabled ? "\033[0;46m" : "";
			BG.GRAY = isEnabled ? "\033[0;47m" : "";

			FG.BLACK = isEnabled ? "\033[0;30m" : "";
			FG.RED = isEnabled ? "\033[0;31m" : "";
			FG.GREEN = isEnabled ? "\033[0;32m" : "";
			FG.YELLOW = isEnabled ? "\033[0;33m" : "";
			FG.BLUE = isEnabled ? "\033[0;34m" : "";
			FG.MAGENTA = isEnabled ? "\033[0;35m" : "";
			FG.CYAN = isEnabled ? "\033[0;36m" : "";
			FG.GRAY = isEnabled ? "\033[0;37m" : "";

			FG.BR_BLACK = isEnabled ? "\033[1;30m" : "";
			FG.BR_RED = isEnabled ? "\033[1;31m" : "";
			FG.BR_GREEN = isEnabled ? "\033[1;32m" : "";
			FG.BR_YELLOW = isEnabled ? "\033[1;33m" : "";
			FG.BR_BLUE = isEnabled ? "\033[1;34m" : "";
			FG.BR_MAGENTA = isEnabled ? "\033[1;35m" : "";
			FG.BR_CYAN = isEnabled ? "\033[1;36m" : "";
			FG.BR_GRAY = isEnabled ? "\033[1;37m" : "";
		}
	}
}
