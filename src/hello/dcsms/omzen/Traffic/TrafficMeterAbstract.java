/*
 * Copyright (C) 2014 Peter Gregus for GravityBox Project (C3C076@xda)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello.dcsms.omzen.Traffic;

import hello.dcsms.omzen.DrawUtils;
import hello.dcsms.omzen.S;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;

public abstract class TrafficMeterAbstract extends TextView implements
		BroadcastSubReceiver {
	protected static final String PACKAGE_NAME = "com.android.systemui";
	protected static final String TAG = "NetworkTraffic";
	protected static final boolean DEBUG = true;

	public static enum TrafficMeterMode {
		OFF, SIMPLE
	};

	protected Context mGbContext;
	protected boolean mAttached;
	protected int mInterval = 1000;
	protected int mPosition;
	protected int mSize;
	protected int mMargin;
	protected boolean mIsScreenOn = true;
	protected boolean mShowOnlyWhenDownloadActive;
	protected boolean mIsDownloadActive;
	private PhoneStateListener mPhoneStateListener;
	private TelephonyManager mPhone;
	protected boolean mMobileDataConnected;
	protected boolean mShowOnlyForMobileData;
	private XSharedPreferences pref;

	protected static void log(String message) {
		// XposedBridge.log(TAG + ": " + message);
	}

	public static TrafficMeterAbstract create(Context context,
			TrafficMeterMode mode) {
		if (mode == TrafficMeterMode.SIMPLE) {
			return new TrafficMeter(context);
		} else {
			throw new IllegalArgumentException(
					"Invalid traffic meter mode supplied");
		}
	}

	protected TrafficMeterAbstract(Context context) {
		super(context);

		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mMargin = 2;
		lParams.setMargins(mMargin, 0, mMargin, 0);
		setLayoutParams(lParams);
		pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();
		setShadowLayer(1, 1, 1, Color.argb(100, 0, 0, 0));
		setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

		if (!isWifiOnly(getContext())) {
			mPhone = (TelephonyManager) getContext().getSystemService(
					Context.TELEPHONY_SERVICE);
			mPhoneStateListener = new PhoneStateListener() {
				@Override
				public void onDataConnectionStateChanged(int state,
						int networkType) {
					final boolean connected = state == TelephonyManager.DATA_CONNECTED;
					if (mMobileDataConnected != connected) {
						mMobileDataConnected = connected;
						if (DEBUG)
							log("onDataConnectionStateChanged: mMobileDataConnected="
									+ mMobileDataConnected);
						updateState();
					}

				}
			};
		}
	}

	private static Boolean mIsWifiOnly = null;

	public static boolean isWifiOnly(Context con) {
		// returns true if device doesn't support mobile data (is wifi only)
		if (mIsWifiOnly != null)
			return mIsWifiOnly;

		try {
			ConnectivityManager cm = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			mIsWifiOnly = (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) == null);
			return mIsWifiOnly;
		} catch (Throwable t) {
			mIsWifiOnly = null;
			return false;
		}
	}

	public void initialize(XSharedPreferences prefs) {
		prefs.reload();
		try {
			mSize = 14;
		} catch (NumberFormatException nfe) {
			log("Invalid preference value for PREF_KEY_DATA_TRAFFIC_SIZE");
		}

		try {
			mPosition = 0;
		} catch (NumberFormatException nfe) {
			log("Invalid preference value for PREF_KEY_DATA_TRAFFIC_POSITION");
		}

		mShowOnlyWhenDownloadActive = false;

		if (mPhone != null) {
			mShowOnlyForMobileData = false;
		}

		onInitialize(prefs);
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null
					&& action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				updateState();
			} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)
					|| action.equals("hello.dcsms.omzen.UPDATETRAFFIC")) {
				onBroadcastReceived(context, intent);
			}
		}
	};

	protected boolean getConnectAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = (connManager != null) ? connManager
				.getActiveNetworkInfo() : null;
		return network != null && network.isConnected();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!mAttached) {
			mAttached = true;
			if (DEBUG)
				log("attached to window");
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction("hello.dcsms.omzen.UPDATETRAFFIC");
			filter.addAction(Intent.ACTION_BOOT_COMPLETED);
			getContext().registerReceiver(mIntentReceiver, filter, null,
					getHandler());

			if (mPhone != null) {
				mPhone.listen(mPhoneStateListener,
						PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
			}

			updateState();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			mAttached = false;
			if (DEBUG)
				log("detached from window");
			getContext().unregisterReceiver(mIntentReceiver);

			if (mPhone != null) {
				mPhone.listen(mPhoneStateListener,
						PhoneStateListener.LISTEN_NONE);
			}

			updateState();
		}
	}

	public int getTrafficMeterPosition() {
		return mPosition;
	}

	@Override
	public void onScreenStateChanged(int screenState) {
		mIsScreenOn = screenState == View.SCREEN_STATE_ON;
		updateState();
		super.onScreenStateChanged(screenState);
	}

	@Override
	public void onBroadcastReceived(Context context, Intent intent) {

		if (intent.equals(Intent.ACTION_BOOT_COMPLETED)||intent.getAction().equals(S.UPDATE_TRAFFIC)) {
			pref = new XSharedPreferences("hello.dcsms.omzen");
			pref.makeWorldReadable();
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
			updateState();
		}

	}

	private boolean shoudStartTrafficUpdates() {
		boolean shouldStart = mAttached && mIsScreenOn && getConnectAvailable();
		if (mShowOnlyWhenDownloadActive) {
			shouldStart &= mIsDownloadActive;
		}
		if (mShowOnlyForMobileData) {
			shouldStart &= mMobileDataConnected;
		}

		shouldStart = getprefvisible();
		return shouldStart;
	}

	private boolean getprefvisible() {
		pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();
		return pref.getBoolean("TRAFFIC", true);
	}

	protected void updateState() {
		if (shoudStartTrafficUpdates()) {
			startTrafficUpdates();
			setVisibility(View.VISIBLE);
			if (DEBUG)
				log("traffic updates started");
		} else {
			stopTrafficUpdates();
			setVisibility(View.GONE);
			setText("");
			if (DEBUG)
				log("traffic updates stopped");
		}
	}

	protected abstract void onInitialize(XSharedPreferences prefs);

	protected abstract void onPreferenceChanged(Intent intent);

	protected abstract void startTrafficUpdates();

	protected abstract void stopTrafficUpdates();
}
