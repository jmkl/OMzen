package hello.dcsms.omzen.Util;

import hello.dcsms.omzen.S;
import hello.dcsms.omzen.theme.ThemeData;
import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

public class JSON {
	public JSON() {
	}

	public static void writeMessage(JsonWriter writer, ThemeData data)
			throws IOException {
		writer.beginObject();
		writer.name(ThemeKontsran.NAMATEMA).value(data.get_NAMATEMA());
		writer.name(ThemeKontsran.PENGRAJIN).value(data.get_PENGRAJIN());
		writer.name(ThemeKontsran.VERSI).value(data.get_VERSI());
		writer.name(ThemeKontsran.KETERANGAN).value(data.get_KETERANGAN());
		writer.name(ThemeKontsran.KONTAK).value(data.get_KONTAK());
		writer.endObject();
	}

	public void inisiasi() {
		FileReader r = null;
		try {
			r = new FileReader(S.DEFAULT_ICON_DIR + "/notif_apps_icon.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (r == null)
			return;
		try {
			List<DATA> ld = new ArrayList<DATA>();
			String namapaket = "";
			List<String> default_icon, nama_icon;
			default_icon = new ArrayList<String>();
			nama_icon = new ArrayList<String>();
			JsonReader reader = new JsonReader(r);
			reader.beginArray();
			while (reader.hasNext()) {
				DATA dd = new DATA();
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (name.equals("package")) {
						namapaket = reader.nextString();
						dd.setNAMA_PAKET(namapaket);
					} else if (name.equals("icon")) {
						reader.beginObject();
						while (reader.hasNext()) {
							String n = reader.nextName();
							if (n.equals("nama")) {
								default_icon.add(reader.nextString());
							} else if (n.equals("ic")) {
								nama_icon.add(reader.nextString());
							} else {
								reader.skipValue();
							}
						}
						dd.setOLD_IC(default_icon);
						dd.setNEW_IC(nama_icon);
						reader.endObject();
					} else {
						reader.skipValue();
					}

				}

				reader.endObject();
				ld.add(dd);
			}
			reader.endArray();
			reader.close();
			for (int i = 0; i < ld.size(); i++) {
				DATA d = ld.get(i);
				Log.e("namapaket :", d.getNAMA_PAKET());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
