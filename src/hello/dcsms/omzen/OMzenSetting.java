package hello.dcsms.omzen;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public class OMzenSetting extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView tvinfo = new WebView(this);
		tvinfo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		tvinfo.loadUrl("file:///android_asset/info.html");
		setContentView(tvinfo);

	}

}
