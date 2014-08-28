package hello.dcsms.omzen.theme;

import hello.dcsms.omzen.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThemeAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> data;
	private static LayoutInflater inflater = null;

	public ThemeAdapter(Activity a, List<String> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.item, null);
		}

		TextView text = (TextView) vi.findViewById(R.id.text);
		;
		ImageView image1 = (ImageView) vi.findViewById(R.id.image1);
		ImageView image2 = (ImageView) vi.findViewById(R.id.image2);
		ThemeInfoData tid = ThemeInfo.getData(data.get(position));
		ThemeData t = tid.gettDATA();
		String info = "nama \t\t\t: " + t.get_NAMATEMA() + "\npengrajin \t\t: "
				+ t.get_PENGRAJIN() + "\nnversi \t\t\t: " + t.get_VERSI()
				+ "\nkontak \t\t\t: " + t.get_KONTAK() + "\nketerangan \t\t: "
				+ t.get_KETERANGAN();

		text.setText(info);
		if (tid.getTdSS() != null) {
			image1.setImageBitmap(tid.getTdSS().get(0));
		}
		image2.setImageBitmap(tid.getTdSS().get(1));
		// imageLoader.DisplayImage(data.get(position), image);
		return vi;
	}
}