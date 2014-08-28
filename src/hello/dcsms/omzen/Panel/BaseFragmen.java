package hello.dcsms.omzen.Panel;

import android.app.Fragment;
import android.util.Log;

public class BaseFragmen extends Fragment {


	public void gantiSub(String subtitle) {

		Log.i("S", "ONCREATEVIEW");
		getActivity().getActionBar().setSubtitle(subtitle);
	};
}
