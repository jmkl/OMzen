package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.Util.JSON;
import hello.dcsms.omzen.Util.ZipUtils;
import hello.dcsms.omzen.theme.ThemeData;
import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BackupThemeFragment extends BaseFragmen implements OnClickListener {
	LinearLayout leot;
	EditText et_nama, et_autor, et_versi, et_kontak, et_keterangan;
	Button ss1, ss2, apply;
	ImageView iv1, iv2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		leot = (LinearLayout) inflater.inflate(R.layout.backuptheme, null);
		findView();
		return leot;
	}

	private ColorStateList statelist() {
		int[][] states = new int[][] {
				new int[] { android.R.attr.state_pressed }, new int[] {} };
		int[] colors = new int[] { Color.RED, Color.WHITE };
		ColorStateList c = new ColorStateList(states, colors);
		return c;
	}

	private void findView() {
		et_nama = (EditText) leot.findViewById(R.id.bt_namatema);
		et_autor = (EditText) leot.findViewById(R.id.bt_namaauthor);
		et_versi = (EditText) leot.findViewById(R.id.bt_versi);
		et_kontak = (EditText) leot.findViewById(R.id.bt_kontak);
		et_keterangan = (EditText) leot.findViewById(R.id.bt_keterangan);
		ss1 = (Button) leot.findViewById(R.id.bt_ss1);
		ss2 = (Button) leot.findViewById(R.id.bt_ss2);
		apply = (Button) leot.findViewById(R.id.bt_apply);
		iv1 = (ImageView) leot.findViewById(R.id.ss1);
		iv2 = (ImageView) leot.findViewById(R.id.ss2);
		ss1.setOnClickListener(this);
		ss2.setOnClickListener(this);
		apply.setOnClickListener(this);

	}

	private String getImagePath(Uri uri) {
		Cursor cursor = getActivity().getContentResolver().query(uri, null,
				null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	private void getImageChooser(String judul, int rekueskode) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.setType("image/*");
		startActivityForResult(Intent.createChooser(i, judul), rekueskode);
	}

	int SS1_REQCODE = 10001;
	int SS2_REQCODE = 10002;
	String SS1 = "", SS2 = "";

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_ss1:
			getImageChooser("Pilih Screenshot 1", SS1_REQCODE);
			break;

		case R.id.bt_ss2:
			getImageChooser("Pilih Screenshot 2", SS2_REQCODE);
			break;
		case R.id.bt_apply:

			boolean boo = sudahbolehdibackup();
			if (boo) {
				ThemeData d = new ThemeData();
				d.set(GT(et_nama), GT(et_autor), GT(et_keterangan),
						GT(et_versi), GT(et_kontak));

				try {
					bikinFileJSON();
					KopiFileSS();
					String[] files = new String[] {
							Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/default_icon",
							ThemeKontsran.OMZENCACHEDIR + "/info.json",
							ThemeKontsran.OMZENCACHEDIR + "/screenshot_1.jpg",
							ThemeKontsran.OMZENCACHEDIR + "/screenshot_2.jpg" };
					ZIPBACKUP zip = new ZIPBACKUP(d);
					zip.execute(files);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}

	}

	private class ZIPBACKUP extends AsyncTask<String, Void, Boolean> {
		ThemeData td;
		ProgressDialog pd;

		public ZIPBACKUP(ThemeData t) {
			td = t;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				pd.dismiss();
				Toast.makeText(getActivity(), "Sukses backup",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setTitle("Backup Tema");
			pd.setMessage("Mohon tunggu sejenak");
			pd.setIndeterminate(true);
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Boolean doInBackground(String... str) {
			return ZipUtils.zipFileAtPath(str, ThemeKontsran.OMZENTHEMEDIR
					+ "/" + td.get_NAMATEMA() + ".omztheme");

		}
	}

	private String GT(EditText et) {
		return et.getText().toString();
	}

	private boolean sudahbolehdibackup() {
		if (GT(et_autor).equals("") || GT(et_keterangan).equals("")
				|| GT(et_kontak).equals("") || GT(et_versi).equals("")
				|| GT(et_nama).equals("")) {
			Toast.makeText(getActivity(), "Semua data harus diisi",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			File f = new File(SS1);
			File f2 = new File(SS2);
			if (!f.exists() || !f2.exists()) {
				Toast.makeText(getActivity(), "Screenshot belon dipilih",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	void KopiFileSS() {

		File ss1 = new File(ThemeKontsran.OMZENCACHEDIR + "/screenshot_1.jpg");
		File ss2 = new File(ThemeKontsran.OMZENCACHEDIR + "/screenshot_2.jpg");
		if (ss1.exists())
			ss1.delete();
		if (ss2.exists())
			ss2.delete();

		File ssbaru1 = new File(SS1);
		File ssbaru2 = new File(SS2);

		if (ssbaru1.exists())
			KOPISS(ssbaru1, "screenshot_1.jpg");
		if (ssbaru2.exists())
			KOPISS(ssbaru2, "screenshot_2.jpg");

	}

	private boolean KOPISS(File iss, String osx) {
		boolean result = false;
		InputStream is = null;
		OutputStream os = null;

		final int buffer_size = 1024;
		try {
			is = new FileInputStream(iss);
			os = new FileOutputStream(new File(ThemeKontsran.OMZENCACHEDIR
					+ "/" + osx));
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
			is.close();
			os.close();
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	void bikinFileJSON() throws IOException {
		File f = new File(ThemeKontsran.OMZENCACHEDIR);
		if (!f.exists())
			f.mkdirs();
		File json = new File(f + "/info.json");
		if (json.exists())
			json.delete();
		FileOutputStream out = new FileOutputStream(json);
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		ThemeData d = new ThemeData();
		d.set(GT(et_nama), GT(et_autor), GT(et_keterangan), GT(et_versi),
				GT(et_kontak));

		JSON.writeMessage(writer, d);
		writer.close();
		out.close();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			setOnPickerResult(requestCode, data);

		}

	}

	public void setOnPickerResult(int requestCode, Intent data) {
		if (requestCode == SS1_REQCODE) {
			Uri uri = data.getData();
			File f = new File(uri.getPath());

			if (!f.isFile()) {
				SS1 = getImagePath(uri);
			} else {
				SS1 = uri.getPath();
			}

			iv1.setImageURI(Uri.fromFile(new File(SS1)));
		} else if (requestCode == SS2_REQCODE) {
			Uri uri = data.getData();
			File f = new File(uri.getPath());

			if (!f.isFile()) {
				SS2 = getImagePath(uri);
			} else {
				SS2 = uri.getPath();
			}
			iv2.setImageURI(Uri.fromFile(new File(SS2)));
		}

	}
}
