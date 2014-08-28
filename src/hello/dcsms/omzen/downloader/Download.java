package hello.dcsms.omzen.downloader;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.Util.ThemeViewer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

public class Download extends AsyncTask<String, Integer, File> {
	static InputStream is = null;
	private Context c;
	Notification.Builder notif;
	NotificationManager mNotificationManager;
	int nID = 666;
	String mB = "B";
	String mKB = "KB";
	String mMB = "MB";
	String mS = "s";
	NumberFormat mDecimalFormat = new DecimalFormat("##0.0");
	NumberFormat mIntegerFormat = NumberFormat.getIntegerInstance();

	private String formatTraffic(long bytes) {
		if (bytes > 10485760) { // 1024 * 1024 * 10
			return (mIntegerFormat.format(bytes / 1048576) + mMB);
		} else if (bytes > 1048576) { // 1024 * 1024
			return (mDecimalFormat.format(((float) bytes) / 1048576f) + mMB);
		} else if (bytes > 10240) { // 1024 * 10
			return (mIntegerFormat.format(bytes / 1024) + mKB);
		} else if (bytes > 1024) { // 1024
			return (mDecimalFormat.format(((float) bytes) / 1024f) + mKB);
		} else {
			return (mIntegerFormat.format(bytes) + mB);
		}
	}

	public Download(Context c) {
		this.c = c;

	}

	@Override
	protected void onPreExecute() {

		notif = new Notification.Builder(c)

		.setContentTitle("OMZTheme ").setContentText("Downloading")
				.setSmallIcon(R.drawable.ic_launcher).setProgress(0, 0, true);
		notif.setOngoing(true);
		mNotificationManager = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(nID, notif.build());
	}

	@Override
	protected void onPostExecute(File result) {
		if (result != null && result.exists()) {
			Intent i = new Intent(c, ThemeViewer.class);			
			i.setData(Uri.fromFile(result));
			TaskStackBuilder stac = TaskStackBuilder.create(c);
			stac.addNextIntent(i);
			stac.addParentStack(ThemeViewer.class);
		
			PendingIntent pi = stac.getPendingIntent(0,
					PendingIntent.FLAG_ONE_SHOT);
			
			notif.setContentIntent(pi);
			notif.setContentText("Please tap this notification to apply patch").setProgress(0, 0, false);
			notif.setTicker("Downloading is done");
			notif.setOngoing(false);
			mNotificationManager.notify(nID, notif.build());
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		notif.setContentText("Downloaded : " + formatTraffic((long) values[0]))
				.setProgress(0, 0, false);
		mNotificationManager.notify(nID, notif.build());
	}

	@Override
	protected File doInBackground(String... ss) {
		File f = new File(ss[1]);
		if (!f.exists() || !f.getName().contains("SampleTheme")) {
			try {
				URL url = new URL(ss[0]);
				URLConnection conn = url.openConnection();
				conn.connect();

				int lenght = conn.getContentLength();
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream out = new FileOutputStream(ss[1]);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) total);
					out.write(data, 0, count);
				}
				out.flush();
				out.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return f;
	}

}
