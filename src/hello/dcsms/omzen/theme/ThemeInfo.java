package hello.dcsms.omzen.theme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ThemeInfo {
	public static ThemeInfoData getData(String namatheme) {
		ThemeInfoData t = new ThemeInfoData();
		try {

			ArrayList<Bitmap> ss = new ArrayList<Bitmap>();

			ZipFile z = new ZipFile(ThemeKontsran.OMZENTHEMEDIR + "/"
					+ namatheme);
			final Enumeration<? extends ZipEntry> entries = z.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (!zipEntry.isDirectory()) {
					String fileName = zipEntry.getName();
					if (fileName.equals("info.json")) {
						InputStream input = z.getInputStream(zipEntry);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(input, "UTF-8"));
						StringBuilder total = new StringBuilder();
						String line = "";
						while ((line = br.readLine()) != null) {
							total.append(line);
						}

						try {
							JSONObject j = new JSONObject(total.toString());
							ThemeData ddx = new ThemeData();

							ddx.set(j.getString(ThemeKontsran.NAMATEMA),
									j.getString(ThemeKontsran.PENGRAJIN),
									j.getString(ThemeKontsran.KETERANGAN),
									j.getString(ThemeKontsran.VERSI),
									j.getString(ThemeKontsran.KONTAK));
							t.settDATA(ddx);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						input.close();
						br.close();
					} else if (fileName.contains("screenshot_")) {
						InputStream input = z.getInputStream(zipEntry);
						Bitmap src = BitmapFactory.decodeStream(input);
						ss.add(src);
					}
				}
			}
			t.setTdSS(ss);
			z.close();
		} catch (IOException e) {
			t = null;
			e.printStackTrace();
		}
		return t;
	}

}
