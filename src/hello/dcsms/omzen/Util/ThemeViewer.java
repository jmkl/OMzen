package hello.dcsms.omzen.Util;

import hello.dcsms.omzen.theme.ThemeKontsran;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class ThemeViewer extends Activity {
	ProgressDialog pd;

	@Override
	protected void onStart() {
		super.onStart();
		pd = new ProgressDialog(this);
		Intent i = getIntent();
		if (i != null) {
			Uri ui = i.getData();
			String namatheme = ui.getEncodedPath();
			AlertDialog.Builder b = new AlertDialog.Builder(this);

			b.setTitle("OMZen Theme");
			b.setMessage("Dengan ini kite apply theme \"" + namatheme
					+ "\" nya gan. Ok?");
			final String namafile = namatheme;
			b.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					finish();

				}
			});
			b.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {
					new UNZIPFILE().execute(namafile);

				}
			});
			b.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface,
						int paramInt) {
					finish();
				}
			});
			b.show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private class UNZIPFILE extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Toast.makeText(
					getApplicationContext(),
					result ? "menginstall sukses, silahkan reboot utk merasakan khasiatnya"
							: "menginstall gagal", Toast.LENGTH_SHORT).show();
			pd.dismiss();
			finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pd.setTitle("Menginstall Tema");
			pd.setMessage("Silahkan tunggu sejenak");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Boolean doInBackground(String... str) {

			 return ZipUtils
			 .unzip(str[0],
			 Environment.getExternalStorageDirectory()
			 .getAbsolutePath());

		}

	}
}
