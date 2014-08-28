package hello.dcsms.omzen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;

public class JamDiStatusBar extends TextView {
	private Context mContext;
	protected boolean mAttached;
	private XSharedPreferences pref;
	private String PKGNAME = "hello.dcsms.omzen";

	public JamDiStatusBar(Context context) {
		super(context);
		mContext = context;
		setGravity(Gravity.CENTER_VERTICAL);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			mAttached = false;
			mContext.unregisterReceiver(mIntentReceiver);

		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(S.UPDATEJAM);
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
			filter.addAction(Intent.ACTION_BOOT_COMPLETED);
			mContext.registerReceiver(mIntentReceiver, filter, null,
					getHandler());
		}
	}

	protected final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(S.UPDATEJAM)
					|| action.equals(Intent.ACTION_BOOT_COMPLETED)
					|| action.equals(Intent.ACTION_TIME_TICK)
					|| action.equals(Intent.ACTION_TIME_CHANGED)
					|| action.equals(Intent.ACTION_TIMEZONE_CHANGED)
					|| action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				updateJam();
			}

		}

	};

	public void updateJam() {
		pref = new XSharedPreferences(PKGNAME);
		pref.makeWorldReadable();
		Date now = new Date();
		boolean uppercase = false;
		String txt = pref.getString("FORMAT_JAM", "^E, HH:mm");
		uppercase = txt.contains("^") ? true : false;
		String ok = txt;
		if (uppercase) {
			ok = ok.replace("^", "");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(ok, Locale.US);
		String currentDateandTime = sdf.format(now);
		if (uppercase) {
			currentDateandTime = currentDateandTime.toUpperCase(Locale.US);
		}

		setText(currentDateandTime);
		int c = Color.WHITE;
		try {
			c = pref.getInt("JAM_WARNA", 0xffffff);

		} catch (Exception e) {

		}
		int size = 16;
		try {
			size = Integer.parseInt(pref.getString("JAM_SIZE", "16"));
		} catch (Exception e) {
		}
		setTextSize(size);
		DrawUtils.setTypeFace(this);
		setTextColor(c);
		setShadowLayer(1, 1, 1, Color.argb(100, 0, 0, 0));
	}
}
