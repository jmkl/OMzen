package hello.dcsms.omzen.Panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MenuFragment extends Fragment {
	private int menuConfig = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			menuConfig = savedInstanceState.getInt("menuConfig");
		}

		List<HashMap<String, String>> d = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> sb = new HashMap<String, String>();
		sb.put("head", "SB Konfig");
		sb.put("sub", "Konfigurasi wilayah statusbar");
		HashMap<String, String> jam = new HashMap<String, String>();
		jam.put("head", "Jam Konfig");
		jam.put("sub", "Konfigurasi format jam");
		HashMap<String, String> theme = new HashMap<String, String>();
		theme.put("head", "Tema Lokal");
		theme.put("sub", "Ganti tema");
		HashMap<String, String> themeol = new HashMap<String, String>();
		themeol.put("head", "Tema Online");
		themeol.put("sub", "Download tema");
		HashMap<String, String> add = new HashMap<String, String>();
		add.put("head", "Pengaturan");
		add.put("sub", "Konfigurasi lainnya");
		HashMap<String, String> help = new HashMap<String, String>();
		help.put("head", "Bantuan");
		help.put("sub",
				"Sebelum grepe-grepe mending baca ini dulu sampai habis");

		HashMap<String, String> backup = new HashMap<String, String>();
		backup.put("head", "Backup");
		backup.put("sub", "Backup tema dan share");
		d.add(sb);
		d.add(jam);
		d.add(theme);
		d.add(themeol);
		d.add(add);
		d.add(help);
		d.add(backup);
		ListView lv = new ListView(getActivity());
		lv.setPadding(10, 20, 10, 10);
		lv.setAdapter(new MenuAdapter(getActivity(), d));
		lv.setOnItemClickListener(onitemklik);

		return lv;
	}

	private OnItemClickListener onitemklik = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> ab, View v, int x, long as) {
			((RootPanel) getActivity()).LoadMenuItem(x);

		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("menuConfig", menuConfig);
	}
}
