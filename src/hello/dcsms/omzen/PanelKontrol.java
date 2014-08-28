package hello.dcsms.omzen;

import hello.dcsms.omzen.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PanelKontrol extends Activity {

	private int BANYAKSTYLE = 12;
	private Button layoutlist, apply;
	private EditText edt_tgl;
	private TextView dump;
	int style = 0;
	SharedPreferences pref;
	boolean formatok = false;
	String[] namastyle;
	Handler h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kontrol);
		namastyle = getResources().getStringArray(R.array.STATBAR_LAYOUT_NAMA);
		// Asset as = new Asset();
		// as.copySample(getApplicationContext());
		pref = getSharedPreferences(S.PKGNAME + "_preferences",
				MODE_WORLD_READABLE);
		getDensity();
		style = pref.getInt(S.STATBAR_LAYOUT_STYLE, 0);
		dump = (TextView) findViewById(R.id.dump);
		dump.setText("Current style: " + namastyle[style]);

		findViewById(R.id.restart).setOnClickListener(restar);
		edt_tgl = (EditText) findViewById(R.id.edt_tgl);
		apply = (Button) findViewById(R.id.apply_tgl);
		apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edt_tgl.getText().toString().equals("")) {
					AlertDialog.Builder adb = new AlertDialog.Builder(v
							.getContext());
					adb.setTitle("Peringatan!");
					adb.setMessage("Kotak format jam kosong. pencet Ok akan membikin jam di statusbar amblas. Untuk format jam. silahkan lihat di menu help");
					adb.setNegativeButton("Batal",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									return;

								}
							});
					adb.setPositiveButton("Ok ngarti!",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Editor edit = pref.edit();
									edit.putString("FORMAT_JAM", edt_tgl
											.getText().toString());
									edit.apply();
									h = new Handler();
									h.postDelayed(new Runnable() {

										@Override
										public void run() {
											Intent ii = new Intent();
											ii.setAction(S.UPDATEJAM);
											sendBroadcast(ii);

										}
									}, 1000);

								}
							});
					adb.show();
				} else {
					Editor edit = pref.edit();
					edit.putString("FORMAT_JAM", edt_tgl.getText().toString());
					edit.apply();

					h = new Handler();
					h.postDelayed(new Runnable() {

						@Override
						public void run() {
							Intent ii = new Intent();
							ii.setAction(S.UPDATEJAM);
							sendBroadcast(ii);

						}
					}, 1000);
				}

			}
		});
		edt_tgl.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				try {
					boolean uppercase = false;

					String txt = edt_tgl.getText().toString();
					txt = txt.replace("=", "");

					uppercase = txt.contains("^") ? true : false;
					String ok = txt;
					if (uppercase) {
						ok = ok.replace("^", "");
					}
					SimpleDateFormat sdf = new SimpleDateFormat(ok,
							getResources().getConfiguration().locale);
					String currentDateandTime = sdf.format(new Date());
					dumptxt("Style Jam", currentDateandTime, uppercase);
					formatok = true;
				} catch (Exception e) {
					dumptxt("ERROR : ", e.getMessage());
					formatok = false;
				}
				apply.setEnabled(formatok);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		StateListDrawable sld = new StateListDrawable();
		sld.addState(new int[] { android.R.attr.state_pressed },
				new ColorDrawable(Color.WHITE));
		sld.addState(new int[] { android.R.attr.state_checked },
				new ColorDrawable(Color.TRANSPARENT));
		sld.addState(new int[] {},
				new ColorDrawable(Color.parseColor("#ff44a8cd")));

		layoutlist = (Button) findViewById(R.id.spinner1);

		layoutlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent();
				i.setAction(S.ATURLAYOUT);
				i.putExtra("STYLE", style);
				sendBroadcast(i);
				dump.setText("Current style: " + namastyle[style]);

				Editor edit = pref.edit();
				edit.putInt(S.STATBAR_LAYOUT_STYLE, style);
				edit.apply();
				style++;
				if (style > BANYAKSTYLE) {
					style = 0;
				}

			}
		});

	}

	private void tesJson() {

	}

	private OnSharedPreferenceChangeListener PreferChange() {
		// TODO Auto-generated method stub
		return null;
	}

	private void getDensity() {
		int dpi = getApplicationContext().getResources().getConfiguration().densityDpi;
		Editor edit = pref.edit();
		edit.putInt("DENSITY", dpi);
		edit.putString("NAMA_DEVICE", android.os.Build.MODEL);
		edit.apply();

	}

	private OnClickListener restar = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent i = new Intent();
			i.setAction(S.OMZEN);
			sendBroadcast(i);

		}
	};

	private void dumptxt(String tag, String msg, boolean... bo) {
		String res = String.format("%s\t:%s", tag, msg);
		if (bo.length > 0)
			if (bo[0]) {
				res = res.toUpperCase(getResources().getConfiguration().locale);
			}

		dump.setText(res);
	}

	@SuppressWarnings("unused")
	private String BacaInfo() {
		StringBuilder buf = new StringBuilder();
		try {

			InputStream json;
			json = getAssets().open("info.txt");

			BufferedReader in = new BufferedReader(new InputStreamReader(json,
					"UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				buf.append(str);
				buf.append("\n");
			}
			in.close();

		} catch (IOException e) {
			buf.append("");
		}
		return buf.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.panel_kontrol, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.restart) {
			Intent i = new Intent(this, OMzenSetting.class);
			startActivity(i);
			return true;
		} else if (id == R.id.restart) {
			Intent i = new Intent(this, Sett.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
