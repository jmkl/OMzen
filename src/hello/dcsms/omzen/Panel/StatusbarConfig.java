package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.S;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusbarConfig extends BaseFragmen implements OnClickListener {
	private int mStatbarConfig = -1;
	SharedPreferences pref;
	int style = 0;
	String[] namastyle;
	private int BANYAKSTYLE = 12;
	private Button layoutlist;
	private TextView dump;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mStatbarConfig = savedInstanceState.getInt("mStatbarConfig");
		}
		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.statusbarfragment, null);
		namastyle = getResources().getStringArray(R.array.STATBAR_LAYOUT_NAMA);
		pref = getActivity().getSharedPreferences(S.PKGNAME + "_preferences",
				getActivity().MODE_WORLD_READABLE);
		getDensity();
		style = pref.getInt(S.STATBAR_LAYOUT_STYLE, 0);
		dump = (TextView) rl.findViewById(R.id.dump);
		layoutlist = (Button) rl.findViewById(R.id.statusbarlayout);
		dump.setText("Current style: " + namastyle[style]);
		rl.findViewById(R.id.statusbarrestart).setOnClickListener(this);
		layoutlist.setOnClickListener(this);
		return rl;
	}

	private void getDensity() {
		int dpi = getActivity().getResources().getConfiguration().densityDpi;
		Editor edit = pref.edit();
		edit.putInt("DENSITY", dpi);
		edit.putString("NAMA_DEVICE", android.os.Build.MODEL);
		edit.apply();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("mStatbarConfig", mStatbarConfig);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.statusbarlayout:
			Intent i = new Intent();
			i.setAction(S.ATURLAYOUT);
			i.putExtra("STYLE", style);
			getActivity().sendBroadcast(i);
			dump.setText("Current style: " + namastyle[style]);

			Editor edit = pref.edit();
			edit.putInt(S.STATBAR_LAYOUT_STYLE, style);
			edit.apply();
			style++;
			if (style > BANYAKSTYLE) {
				style = 0;
			}
			break;

		case R.id.statusbarrestart:
			Intent ix = new Intent();
			ix.setAction(S.OMZEN);//S.OMZEN);
			getActivity().sendBroadcast(ix);
			break;
		}

	}
}
