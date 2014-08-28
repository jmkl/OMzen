package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.Util.ZipUtils;
import hello.dcsms.omzen.coverflu.CoverFluGL;
import hello.dcsms.omzen.coverflu.CoverFluGL.CoverFluListener;
import hello.dcsms.omzen.theme.ThemeData;
import hello.dcsms.omzen.theme.ThemeInfo;
import hello.dcsms.omzen.theme.ThemeInfoData;
import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ThemeFragment extends BaseFragmen {
	private int mThemeFrag = -1;
	private TextView txt_info;
	private FrameLayout parentLayout;
	private CoverFluGL mCoverFlu;
	List<String> themefile = new ArrayList<String>();
	ArrayList<HashMap<String, String>> themeinfo = new ArrayList<HashMap<String, String>>();
	List<Bitmap> themepreview = new ArrayList<Bitmap>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mThemeFrag = savedInstanceState.getInt("mThemeFrag");
		}
		File f = new File(ThemeKontsran.OMZENTHEMEDIR);
		if (!f.exists()) {
			f.mkdirs();
		}

		parentLayout = new FrameLayout(getActivity());
		parentLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));

		txt_info = new TextView(getActivity());
		txt_info.setTextColor(Color.WHITE);
		txt_info.setTextSize(12f);
		txt_info.setPadding(20, 0, 0, 0);
		new getThemeData(new ThemeListener()).execute();
		return parentLayout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mThemeFrag", mThemeFrag);
	}

	private static interface ProcessingListener {
		public void onComplete();
	}

	private class ThemeListener implements ProcessingListener {

		@Override
		public void onComplete() {
			mCoverFlu = new CoverFluGL(getActivity());
			mCoverFlu.setBackgroundTexture(R.drawable.default_wallpaper);
			parentLayout.addView(mCoverFlu, new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 20, 0, 0);
			parentLayout.addView(txt_info, params);
			mCoverFlu.setCoverFluListener(new ThemeGLCoverGluListener());
			mCoverFlu.setSelection(0);
			mCoverFlu.setSensitivity(3.0f);
			// setContentView(parentLayout);

		}

	}

	private String[][] ICONARRAY = new String[][] { ThemeKontsran.BATRAI_ICON,
			ThemeKontsran.SINYAL_ICON, ThemeKontsran.INOUT_ICON,
			ThemeKontsran.TOGGLE_ICON, ThemeKontsran.TOOGLEBG,
			ThemeKontsran.NOTIFIMAGE, ThemeKontsran.STATUSBAR_BG,
			ThemeKontsran.STATUSBAR_ICON };

	private String[] ItemTema = new String[] { "Baterai Icons", "Sinyal Icons",
			"InOut Icons", "Toggles Icons", "Toggles Backgrounds",
			"Notification Background", "Statusbar Background",
			"Statusbar Icons Lain-lain" };
	private boolean[] itemstat = new boolean[] { true, true, true, true, true,
			true, true, true };

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
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					txt_info.setText(themeinfo.get(position).get("info"));
				}
			});
		}

		@Override
		public void topTileClicked(final CoverFluGL view, int position) {
			AlertDialog.Builder b = new AlertDialog.Builder(view.getContext());
			final String namafile = themeinfo.get(position).get("file");
			View v = LayoutInflater.from(view.getContext()).inflate(
					R.layout.mixtheme, null);
			b.setView(v);
			final LinearLayout lpar = (LinearLayout) v
					.findViewById(R.id.cekparent);
			// b.setMultiChoiceItems(ItemTema, itemstat,
			// new OnMultiChoiceClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface arg0, int pos,
			// boolean bool) {
			//
			// itemstat[pos] = bool;
			//
			// }
			// });
			b.setTitle("OMZ Mix Theme");

			b.setPositiveButton("Hapus Tema",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface d, int arg1) {
							AlertDialog.Builder b = new AlertDialog.Builder(
									view.getContext());
							b.setIcon(android.R.drawable.ic_dialog_alert);
							b.setTitle("Hapus " + namafile);
							b.setMessage("Apus nih?");
							b.setNeutralButton("Yap", new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									File f = new File(
											ThemeKontsran.OMZENTHEMEDIR + "/"
													+ namafile);
									if (f.exists()) {
										f.delete();
										FragmentTransaction t = getFragmentManager()
												.beginTransaction();
										ThemeFragment tf = new ThemeFragment();

										t.replace(R.id.fragroot, tf);
										t.commit();
										Toast.makeText(view.getContext(),
												"Berhasil Dihapus",
												Toast.LENGTH_SHORT).show();

									}

								}
							});

							b.show();

						}
					});

			b.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {
					for (int i = 0; i < lpar.getChildCount(); i++) {
						CheckBox cb = (CheckBox) lpar.getChildAt(i);
						itemstat[i] = cb.isChecked();
					}
					new UNZIPFILE(itemstat).execute(namafile);

				}
			});
			b.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {

				}
			});

			b.show();

		}

	}

	private class UNZIPFILE extends AsyncTask<String, Void, Boolean> {
		ProgressDialog pd;
		private boolean[] iconstate;

		public UNZIPFILE(boolean[] itemstat) {
			iconstate = itemstat;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Toast.makeText(
					getActivity(),
					result ? "menginstall sukses, silahkan reboot utk merasakan khasiatnya"
							: "menginstall gagal", Toast.LENGTH_SHORT).show();
			pd.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setTitle("Menginstall Tema");
			pd.setMessage("Silahkan tunggu sejenak");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Boolean doInBackground(String... str) {
			List<String> all = new ArrayList<String>();

			for (int i = 0; i < iconstate.length; i++) {
				if (iconstate[i])
					all.addAll(Arrays.asList(ICONARRAY[i]));
			}
			String[] boo = all.toArray(new String[all.size()]);
			if (boo != null)
				return ZipUtils.unzip(boo, ThemeKontsran.OMZENTHEMEDIR + "/"
						+ str[0], Environment.getExternalStorageDirectory()
						.getAbsolutePath());
			else
				return false;

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
						+ "\nversi \t\t\t: " + t.get_VERSI()
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
