package hello.dcsms.omzen.Util;

import hello.dcsms.omzen.S;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Asset {
	public void copySample(Context context) {
		String def_icon_dir = S.DEFAULT_ICON_DIR;// Environment.getExternalStorageDirectory().getAbsolutePath()+"/default_icon";
		File defICONDIR = new File(def_icon_dir);
		if (!defICONDIR.exists()) {
			defICONDIR.mkdirs();
		}
		defICONDIR.setReadable(true, false);
		defICONDIR.setWritable(true, false);

		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("icon");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		for (String filename : files) {
			File f = new File(def_icon_dir + "/" + filename);
			if (!f.exists()) {

				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open("icon/" + filename);
					out = new FileOutputStream(new File(def_icon_dir, filename));

					byte[] buf = new byte[1024];
					int len;
					try {
						while ((len = in.read(buf, 0, buf.length)) != -1) {
							out.write(buf, 0, len);
						}
					} finally {
						in.close();
						out.close();
					}

					f.setWritable(true, false);
					f.setReadable(true, false);
				} catch (Exception e) {
					Log.e("tag", e.getMessage());
				}
			}
		}

	}

}
