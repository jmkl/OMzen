package hello.dcsms.omzen.Panel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class HelpFragment extends BaseFragmen {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WebView tvinfo = new WebView(getActivity());
		tvinfo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		tvinfo.loadUrl("file:///android_asset/info.html");
		return tvinfo;
	}

	
}
