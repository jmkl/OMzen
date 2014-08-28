package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.S;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingFragment extends PreferenceFragment implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private int mSettFrag = -1;
	private SharedPreferences prefs;

	public void gantiSub(String subtitle) {
		getActivity().getActionBar().setSubtitle(subtitle);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mSettFrag = savedInstanceState.getInt("mSettingFrag");
		}
		addPreferencesFromResource(R.xml.pref);
		prefs = getActivity().getSharedPreferences(S.PKGNAME + "_preferences",
				getActivity().MODE_WORLD_READABLE);

		prefs.registerOnSharedPreferenceChangeListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("mSettingFrag", mSettFrag);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref,
			final String key) {
		Activity ac = getActivity();
		if(ac==null)
			return;
		final ProgressDialog d = new ProgressDialog(ac);
		d.setTitle("Loading");
		d.setMessage("Silahkan tunggu sejenak");
		d.setIndeterminate(true);
		d.show();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent i = null, i2 = null;
				if (key.equals("JAM_WARNA") || key.equals("JAM_SIZE")) {
					i = new Intent();
					i2 = new Intent();
					i.setAction(S.UPDATEJAM);
					i2.setAction(S.UPDATE_TRAFFIC);
				} else if (key.equals("TRAFFIC")) {
					i = new Intent();
					i.setAction(S.UPDATE_TRAFFIC);
				} else if (key.equals("OMNOTIFIKASI")
						|| key.equals("ICON_MERGE_WIDTH")) {
					i = new Intent();
					i.putExtra("STYLE", prefs.getInt(S.STATBAR_LAYOUT_STYLE, 0));
					i.setAction(S.ATURLAYOUT);
					
				} else if (key.equals("SMALL_SINYAL")) {
					i = new Intent();
					i.setAction("hello.dcsms.omzen.UPDATESINYAL");
				} else if (key.equals("WARNA_JAM_NOTIF")
						|| key.equals("WARNA_QS_NOTIF")) {
					i = new Intent();
					i.setAction("hello.dcsms.omzen.UPDATENOTIFTEXT");
				}

				Intent[] ix = new Intent[] { i, i2 };
				for (Intent intent : ix) {
					if (intent != null)
						getActivity().sendBroadcast(intent);
				}
				d.dismiss();
			}
		}, 1000);

		Log.i(getActivity().getClass().getCanonicalName(), key);

	}

}
