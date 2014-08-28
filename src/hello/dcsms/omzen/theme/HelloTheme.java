package hello.dcsms.omzen.theme;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.Util.ZipUtils;
import hello.dcsms.omzen.coverflu.CoverFluGL;
import hello.dcsms.omzen.coverflu.CoverFluGL.CoverFluListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class HelloTheme extends Activity {
	private TextView txt_info;
	private FrameLayout parentLayout;
	private CoverFluGL mCoverFlu;
	List<String> themefile = new ArrayList<String>();
	ArrayList<HashMap<String, String>> themeinfo = new ArrayList<HashMap<String, String>>();
	List<Bitmap> themepreview = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File f = new File(ThemeKontsran.OMZENTHEMEDIR);
		if (!f.exists()) {
			f.mkdirs();
		}

		parentLayout = new FrameLayout(this);
		parentLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		txt_info = new TextView(this);
		txt_info.setTextColor(Color.WHITE);
		txt_info.setTextSize(15f);
		new getThemeData(new ThemeListener()).execute();
	}

	private static interface ProcessingListener {
		public void onComplete();
	}

	private class ThemeListener implements ProcessingListener {

		@Override
		public void onComplete() {
			mCoverFlu = new CoverFluGL(HelloTheme.this);
			parentLayout.addView(mCoverFlu, new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 20, 0, 0);
			parentLayout.addView(txt_info, params);
			mCoverFlu.setCoverFluListener(new ThemeGLCoverGluListener());
			mCoverFlu.setSelection(0);
			mCoverFlu.setSensitivity(3.0f);
			setContentView(parentLayout);

		}

	}

	private class ThemeGLCoverGluListener implements CoverFluListener {

		@Override
		public int getCount(CoverFluGL view) {
			return themepreview.size();
		}

		@Override
		public Bitmap getImage(CoverFluGL anotherCoverFlow, int position) {
			return themepreview.get(position);
		}

		@Override
		public void tileOnTop(CoverFluGL view, final int position) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					txt_info.setText(themeinfo.get(position).get("info"));
				}
			});
		}

		@Override
		public void topTileClicked(CoverFluGL view, int position) {
			AlertDialog.Builder b = new AlertDialog.Builder(view.getContext());

			b.setTitle("OMZen Theme");
			b.setMessage("Dengan ini kite apply theme \""
					+ themeinfo.get(position).get("file") + "\" nya gan. Ok?");
			final String namafile = themeinfo.get(position).get("file");
			b.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {
					ZipUtils.unzip(ThemeKontsran.OMZENTHEMEDIR + "/"
							+ namafile, Environment
							.getExternalStorageDirectory()
							.getAbsolutePath());
					Toast.makeText(getApplicationContext(), "sukses",
							Toast.LENGTH_SHORT).show();

					Handler h = new Handler();

				}
			});
			b.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {
					// TODO Auto-generated method stub

				}
			});
			b.show();

		}

	}

	private class getThemeData extends AsyncTask<Void, Void, Void> {
		private ProcessingListener listener;

		public getThemeData(ThemeListener themeListener) {
			this.listener = themeListener;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			File files = new File(ThemeKontsran.OMZENTHEMEDIR);
			if (files.isDirectory()) {
				File[] filesx = files.listFiles();
				for (File file : filesx) {
					if (file.getName().endsWith("omztheme")) {
						themefile.add(file.getName());
					}
				}
			}
			for (String theme : themefile) {
				ThemeInfoData tid = ThemeInfo.getData(theme);
				ThemeData t = tid.gettDATA();
				String info = "nama \t\t\t: " + t.get_NAMATEMA()
						+ "\npengrajin \t\t: " + t.get_PENGRAJIN()
						+ "\nnversi \t\t\t: " + t.get_VERSI()
						+ "\nkontak \t\t\t: " + t.get_KONTAK()
						+ "\nketerangan \t: " + t.get_KETERANGAN();
				HashMap<String, String> ti = new HashMap<String, String>();

				if (tid.getTdSS() != null) {
					for (Bitmap bmp : tid.getTdSS()) {
						themepreview.add(bmp);
						ti.put("info", info);
						ti.put("file", theme);
						themeinfo.add(ti);
					}

				} else {
					themepreview.add(BitmapFactory.decodeResource(
							getResources(), R.drawable.ic_launcher));
					ti.put("info", info);
					ti.put("file", theme);
					themeinfo.add(ti);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listener.onComplete();
		}

	}

}
