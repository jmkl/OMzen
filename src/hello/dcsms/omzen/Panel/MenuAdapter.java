package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	private Activity activity;
	private List<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public MenuAdapter(Activity a, List<HashMap<String, String>> d) {
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
		TextView text_head = (TextView) vi.findViewById(R.id.txthead);
		TextView text_sub = (TextView) vi.findViewById(R.id.txtsub);
		HashMap<String, String> menu = data.get(position);
		text_head.setText(menu.get("head"));
		text_sub.setText(menu.get("sub"));
		return vi;
	}
}