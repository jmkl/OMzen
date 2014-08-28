package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.S;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JamFragment extends BaseFragmen implements OnClickListener,
		TextWatcher {
	private int mStatbarConfigJam = -1;
	private Button apply;
	private EditText edt_tgl;
	private TextView dump;
	SharedPreferences pref;
	boolean formatok = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mStatbarConfigJam = savedInstanceState.getInt("mStatbarConfigJam");
		}
		pref = getActivity().getSharedPreferences(S.PKGNAME + "_preferences",
				getActivity().MODE_WORLD_READABLE);
		RelativeLayout rl = (RelativeLayout) inflater.inflate(
				R.layout.jamfragment, null);
		dump = (TextView) rl.findViewById(R.id.dumptext);
		apply = (Button) rl.findViewById(R.id.apply_tgl);
		edt_tgl = (EditText) rl.findViewById(R.id.edt_tgl);
		apply.setOnClickListener(this);
		edt_tgl.addTextChangedListener(this);
		edt_tgl.setText(pref.getString("FORMAT_JAM", ""));
		return rl;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("mStatbarConfigJam", mStatbarConfigJam);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		try {
			boolean uppercase = false;

			String txt = edt_tgl.getText().toString();
			txt = txt.replace("=", "");

			uppercase = txt.contains("^") ? true : false;
			String ok = txt;
			if (uppercase) {
				ok = ok.replace("^", "");
			}
			SimpleDateFormat sdf = new SimpleDateFormat(ok, getResources()
					.getConfiguration().locale);
			String currentDateandTime = sdf.format(new Date());
			dumptxt("", currentDateandTime, uppercase);
			formatok = true;
		} catch (Exception e) {
			dumptxt("ERROR : ", e.getMessage());
			formatok = false;
		}
		apply.setEnabled(formatok);

	}

	private void dumptxt(String tag, String msg, Boolean... bo) {
		String res = String.format("%s %s", tag, msg);
		if (bo.length > 0)
			if (bo[0]) {
				res = res.toUpperCase(getResources().getConfiguration().locale);
			}

		dump.setText(res);

	}

	@Override
	public void onClick(View v) {
		if (edt_tgl.getText().toString().equals("")) {
			AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
			adb.setTitle("Peringatan!");
			adb.setMessage("Kotak format jam kosong. pencet Ok akan membikin jam di statusbar amblas. Untuk format jam. silahkan lihat di menu help");
			adb.setNegativeButton("Batal",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							return;

						}
					});
			adb.setPositiveButton("Ok ngarti!",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Editor edit = pref.edit();
							edit.putString("FORMAT_JAM", edt_tgl.getText()
									.toString());
							edit.apply();
							Handler h = new Handler();
							h.postDelayed(new Runnable() {

								@Override
								public void run() {
									Intent ii = new Intent();
									ii.setAction(S.UPDATEJAM);
									getActivity().sendBroadcast(ii);

								}
							}, 1000);

						}
					});
			adb.show();
		} else {
			Editor edit = pref.edit();
			edit.putString("FORMAT_JAM", edt_tgl.getText().toString());
			edit.apply();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent ii = new Intent();
					ii.setAction(S.UPDATEJAM);
					getActivity().sendBroadcast(ii);
					
					Intent i = new Intent();
					i.setAction(S.UPDATE_TRAFFIC);
					getActivity().sendBroadcast(i);
				}
			}, 1000);
		}
	}

	
}
