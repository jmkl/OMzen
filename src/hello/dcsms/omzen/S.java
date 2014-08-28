package hello.dcsms.omzen;

import android.os.Environment;
import de.robv.android.xposed.XposedBridge;

public class S {
	private static boolean logenable = true;

	public static void log(String msg) {
		if (logenable) {
			XposedBridge.log(msg);
		}
	}

	public static void log_i(int i) {
		if (logenable) {
			XposedBridge.log(Integer.toString(i));
		}
	}

	public static final String OMZEN = "hello.dcsms.omzen.OMZEN";
	public static final String DEFAULT_ICON_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/default_icon";
	protected static final String SYSTEMUI = "com.android.systemui";

	public static final String ATURLAYOUT = "hello.dcsms.omzen.ATURLAYOUT";
	public static final String UPDATEJAM = "hello.dcsms.omzen.UPDATEJAM";
	public static final String UPDATE_TRAFFIC = "hello.dcsms.omzen.UPDATETRAFFIC";

	// PREFERENCE;
	public static String PKGNAME = "hello.dcsms.omzen";
	public static final String STATBAR_LAYOUT_STYLE = "STATUSBAR_LAYOUT";
}
