package hello.dcsms.omzen.serajrblur;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

public class DisplayUtils {

	private static int mBottomOffset = 0;
	private static int mRightOffset = 0;
	private static int mTopOffset = 0;

	public static void setFullScreenActivity(Window window, View view) {

		int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

		// setam os flags
		window.getDecorView().setSystemUiVisibility(flags);

		// setam as margens
		view.setPadding(0, mTopOffset, mRightOffset, mBottomOffset);
		view.requestLayout();

	}

	public static boolean deviceHasOnScreenButtons(Context context) {

		return !ViewConfiguration.get(context).hasPermanentMenuKey();

	}

	public static int getActionBarHeight(Context context) {

		TypedArray styledAttributes = context.getTheme()
				.obtainStyledAttributes(
						new int[] { android.R.attr.actionBarSize });
		int actionBarSize = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();

		return actionBarSize;

	}

	public static void updateConfiguration(Context context, Display display,
			int actionBarHeight) {

		Resources res = context.getResources();
		boolean landscape = display.getRotation() == Configuration.ORIENTATION_LANDSCAPE ? true
				: false;

		// top
		int statusBarHeightResId = res.getIdentifier("status_bar_height",
				"dimen", "android");
		mTopOffset = statusBarHeightResId > 0 ? res
				.getDimensionPixelSize(statusBarHeightResId) : 0;

		// adiciona a action bar ao top
		mTopOffset = mTopOffset + actionBarHeight;

		// bottom
		int navigationBarHeightResId = res.getIdentifier(
				"navigation_bar_height", "dimen", "android");
		int bottomOffset = navigationBarHeightResId > 0 ? res
				.getDimensionPixelSize(navigationBarHeightResId) : 0;

		// não tem os botões na tela !!!
		if (!deviceHasOnScreenButtons(context)) {
			bottomOffset = 0;
		}

		// landscape ?
		if (landscape) {

			mRightOffset = 0;
			mBottomOffset = bottomOffset;
			return;

		}

		Point point = new Point();
		Point point1 = new Point();
		display.getSize(point);
		display.getRealSize(point1);

		// inverte ?
		if (point.x < point1.x) {

			mRightOffset = bottomOffset;
			mBottomOffset = 0;
			return;

		} else {

			mRightOffset = 0;
			mBottomOffset = bottomOffset;
			return;

		}
	}
}