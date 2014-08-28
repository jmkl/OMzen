package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.S;
import hello.dcsms.omzen.downloader.Download;
import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class RootPanel extends FragmentActivity {

	protected HelpFragment mFrag;
	protected MenuFragment menuFrag;
	private BackupThemeFragment btfragment;
	DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private boolean allowexit = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.panel_kontrol, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragframe);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		SharedPreferences pref = getSharedPreferences(S.PKGNAME
				+ "_preferences", Context.MODE_WORLD_READABLE);
		Editor edit = pref.edit();
		edit.apply();

		mDrawerLayout.setDrawerShadow(R.drawable.shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
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
		mDrawerList.setAdapter(new MenuAdapter(this, d));
		mDrawerList.setOnItemClickListener(onitemklik);

		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.ab_bg));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("");
		getActionBar().setSubtitle("");
		cekAPAdefaulticonsudahAdaAtoBelon();
		getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_omzen));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.hello_world,
				R.string.hello_world) {
			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null)
			LoadMenuItem(5);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private OnItemClickListener onitemklik = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> ab, View v, int x, long as) {
			LoadMenuItem(x);

		}
	};

	void dodownloadtheme() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Welcome");
		dialog.setMessage("OMZen need to download some stuff. Make sure your internet connection is on");
		dialog.setCancelable(false);
		// dialog.setNeutralButton("Download dari applikasi",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface paramDialogInterface,
		// int paramInt) {
		// FragmentTransaction t = getFragmentManager()
		// .beginTransaction();
		// OnlineThemeFragment olt = new OnlineThemeFragment();
		// t.replace(R.id.fragroot, olt);
		// getActionBar().setSubtitle("Download tema");
		// t.commit();
		//
		// }
		// });
		dialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface paramDialogInterface,
					int paramInt) {
				Download d = new Download(getApplicationContext());
				d.execute(
						"https://www.facebook.com/download/545703998886470/SampleTheme.omztheme",
						ThemeKontsran.OMZENTHEMEDIR + "/SampleTheme.omztheme");
//				Uri uriUrl = Uri
//						.parse("https://www.facebook.com/download/545703998886470/SampleTheme.omztheme");
//				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//				startActivity(launchBrowser);
				finish();
				// Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);

			}
		});

		dialog.show();

	}

	private void cekAPAdefaulticonsudahAdaAtoBelon() {
		File f = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/default_icon");
		if (f.exists() && f.isDirectory()) {
			File[] files = f.listFiles();
			if (files.length < 160)
				dodownloadtheme();
		} else
			dodownloadtheme();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_restart:
			AlertDialog.Builder d = new AlertDialog.Builder(this);
			d.setTitle("Restart SystemUI");
			d.setMessage("Sebelum restart systemui, toggle Owner harus dinonaktifkan terlebih dahulu untuk mencegah systemui Force close. Apabila sudah nonaktif, silahkan pencet tombol Restart SystemUI");
			d.setNeutralButton("Nonaktifkan Dulu",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							Intent ii = new Intent();
							ii.setClassName("com.android.settings",
									"com.android.settings.Settings$QuickSettingsCheckersSettingsActivity");
							startActivity(ii);

						}
					});
			d.setPositiveButton("Restart SystemUI",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							Intent ii = new Intent();
							ii.setAction(S.OMZEN);
							sendBroadcast(ii);

						}
					});
			d.show();
			return true;
		default:
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			if (drawerOpen)
				mDrawerLayout.closeDrawer(mDrawerList);
			else
				mDrawerLayout.openDrawer(mDrawerList);
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (allowexit) {
			super.onBackPressed();
		} else {
			Toast.makeText(getApplicationContext(),
					"Tekan tombol back sekali lagi untuk keluar",
					Toast.LENGTH_SHORT).show();
			allowexit = true;
			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					allowexit = false;

				}
			}, 3000);
		}

	}

	public void LoadMenuItem(int s) {
		FragmentTransaction t = getFragmentManager().beginTransaction();

		switch (s) {
		case 0:
			StatusbarConfig sbc = new StatusbarConfig();

			t.replace(R.id.fragroot, sbc);
			getActionBar().setSubtitle("Konfigurasi layout statusbar");

			break;
		case 1:
			JamFragment jf = new JamFragment();

			t.replace(R.id.fragroot, jf);
			getActionBar().setSubtitle("Konfigurasi format jam");
			break;
		case 2:
			ThemeFragment tf = new ThemeFragment();

			t.replace(R.id.fragroot, tf);
			getActionBar().setSubtitle("Ganti tema");
			break;
		case 3:
			Intent i = new Intent(this, ONLINE.class);
			startActivity(i);

			break;
		case 4:
			SettingFragment sf = new SettingFragment();
			t.replace(R.id.fragroot, sf);
			getActionBar().setSubtitle("Konfigurasi lain-lain");
			break;
		case 5:
			HelpFragment hf = new HelpFragment();
			t.replace(R.id.fragroot, hf);
			getActionBar().setSubtitle("Bantuan untuk pemula");
			break;
		case 6:
			btfragment = new BackupThemeFragment();
			t.replace(R.id.fragroot, btfragment);
			getActionBar().setSubtitle("Backup tema & Share");
			break;

		}
		getActionBar().setTitle("");
		getActionBar().setSubtitle("");
		t.commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}, 100);

	}

}
