package hello.dcsms.omzen.theme;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.Util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class CopyOfHelloTheme extends Activity {
	private ListView lv;
	List<String> themefile = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme);
		lv = (ListView) findViewById(R.id.themelist);
		cekingdir();
		File files = new File(ThemeKontsran.OMZENTHEMEDIR);
		if (files.isDirectory()) {
			File[] filesx = files.listFiles();
			for (File file : filesx) {
				if (file.getName().endsWith("omztheme")) {
					themefile.add(file.getName());
				}
			}
		}
		ThemeAdapter adapter = new ThemeAdapter(this, themefile);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				AlertDialog.Builder b = new AlertDialog.Builder(paramView
						.getContext());

				b.setTitle("OMZen Theme");
				b.setMessage("Dengan ini kite apply theme \""
						+ themefile.get(paramInt) + "\" nya gan. Ok?");
				final String namafile = themefile.get(paramInt);
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
				b.setNegativeButton("Batal",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								// TODO Auto-generated method stub

							}
						});
				b.show();

			}
		});

	}

	private void cekingdir() {
		File f = new File(ThemeKontsran.OMZENTHEMEDIR);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

}
