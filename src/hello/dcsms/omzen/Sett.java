package hello.dcsms.omzen;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.theme.HelloTheme;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Sett extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		PreferenceScreen pre = (PreferenceScreen) findPreference("OMZ_THEME");
		pre.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), HelloTheme.class);
				startActivity(i);

				return false;
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String s) {
		if (s.equals("TES")) {
			String ss = pref.getString("TES", "0");
			Editor edit = pref.edit();
			edit.putInt("STATUSBAR_LAYOUT", Integer.parseInt(ss));
			edit.apply();
			String[] sa = getResources().getStringArray(
					R.array.STATBAR_LAYOUT_NAMA);
			Toast.makeText(
					getApplicationContext(),
					"Layout \"" + sa[Integer.parseInt(ss)]
							+ "\" has been crot, pres bek baten tu aplai",
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent();
			i.setAction(S.ATURLAYOUT);
			i.putExtra("STYLE", Integer.parseInt(ss));
			sendBroadcast(i);
		}

	}
}
