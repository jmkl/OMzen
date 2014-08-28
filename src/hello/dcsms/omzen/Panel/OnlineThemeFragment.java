package hello.dcsms.omzen.Panel;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.downloader.Download;
import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class OnlineThemeFragment extends BaseFragmen {

	public static void cekKeyhash(Context c) {
		try {
			PackageInfo info = c.getPackageManager().getPackageInfo(
					"hello.dcsms.omzen", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	private int ol = -1;
	private static final ArrayList<String> PERMISSIONS = new ArrayList<String>() {
		{
			add("user_groups");
			add("public_profile");
		}
	};
	private ProgressDialog pdialog;
	private String sdcard = Environment.getExternalStorageDirectory() + "/";
	private String app_id = "304153656422785";
	private String repo = "https://graph.facebook.com/v2.0/317403811760151?fields=files";
	private LinearLayout llonline;
	Intent intent;
	ListView lvtheme;
	List<OLTData> oltdatas;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("oltheme", ol);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			ol = savedInstanceState.getInt("oltheme");
		}
		llonline = (LinearLayout) inflater.inflate(R.layout.onlinetheme, null);
		lvtheme = (ListView) llonline.findViewById(R.id.list_theme);
		lvtheme.setOnItemClickListener(ItemClick);
		Session.openActiveSession(getActivity(), true, sessCallback);

		return llonline;
	}

	private OnItemClickListener ItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
			final OLTData dd = oltdatas.get(pos);
			final String url = dd.getURLTEMA();
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(dd.isDOWNLOADED() ? "Tema ini sudah ada, yakin mau download ulang?"
					: "Dengan ini kite donlot themenya.. pastikan kuota cukup. dan baca bismillah");
			dialog.setTitle("Mengunduh Tema");
			dialog.setNegativeButton("Nope!",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub

						}
					});
			dialog.setPositiveButton("Yap",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							Download dl = new Download(getActivity());
							dl.execute(url, ThemeKontsran.OMZENTHEMEDIR + "/"
									+ dd.getNAMATEMA());

						}
					});
			dialog.show();
		}
	};

	private Session.StatusCallback sessCallback = new Session.StatusCallback() {

		// callback when session changes state
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// make request to the /me API
			if (session.isOpened()) {
				if (sessionHasNecessaryPerms(session)) {

					Bundle d = new Bundle();
					d.putString("fields",
							"files.limit(100).fields(download_link,from,message,updated_time)");
					new Request(session, "317403811760151", d, HttpMethod.GET,
							new Request.Callback() {
								public void onCompleted(Response response) {

									// "data": [
									// {
									// "download_link":
									// "https://www.facebook.com/download/533885603379189/dummyfile.txt",
									// "from": {
									// "id": "1397987382",
									// "name": "Jul Cuih"
									// },
									// "updated_time":
									// "2014-08-06T08:48:53+0000",
									// "id": "318092155024650"
									// },
								
									oltdatas = new ArrayList<OnlineThemeFragment.OLTData>();
									String jsonStr = response.getRawResponse();
									if (jsonStr != null) {
										try {
											JSONObject j = new JSONObject(
													jsonStr);
											JSONObject ja = (JSONObject) j
													.get("files");
											JSONArray jar = ja
													.getJSONArray("data");

											for (int i = 0; i < jar.length(); i++) {
												JSONObject jx = jar
														.getJSONObject(i);
												String lnk = jx
														.getString("download_link");
												Log.i("LINK TEMA DEFAULT", lnk);
												JSONObject juser = jx
														.getJSONObject("from");
												String uploader = juser
														.getString("name");
												OLTData data = new OLTData();
												String namatema = lnk.substring(lnk
														.lastIndexOf("/") + 1);
												if (namatema
														.contains(".omztheme")) {
													data.setURLTEMA(lnk);
													data.setNAMATEMA(namatema);
													data.setNAMAUPLOADER(uploader);
													data.setDOWNLOADED(cekisdownloaderornot(namatema));
													oltdatas.add(data);
												}
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
										}
											try {
												lvtheme.setAdapter(new OLTAdapter(
														getActivity(), oltdatas));
											} catch (Exception e) {
												e.printStackTrace();
											}

									}

								}

							}).executeAsync();

					Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {

										TextView welcome = (TextView) llonline
												.findViewById(R.id.user);
										welcome.setText("Hello "
												+ user.getName() + "!");

									}
								}
							}).executeAsync();
				} else {
					session.requestNewReadPermissions(new NewPermissionsRequest(
							getActivity(), PERMISSIONS));
				}
			}
		}
	};

	private boolean cekisdownloaderornot(String string) {
		File[] themes = new File(ThemeKontsran.OMZENTHEMEDIR).listFiles();
		boolean downloaded = false;
		for (File file : themes) {
			if (file.getName().endsWith("omztheme")) {
				if (file.getName().equals(string))
					downloaded = true;
			}
		}
		return downloaded;
	}

	private boolean sessionHasNecessaryPerms(Session session) {
		if (session != null && session.getPermissions() != null) {
			for (String requestedPerm : PERMISSIONS) {
				if (!session.getPermissions().contains(requestedPerm)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private class OLTData {
		String URLTEMA;

		public String getURLTEMA() {
			return URLTEMA;
		}

		public void setURLTEMA(String uRLTEMA) {
			URLTEMA = uRLTEMA;
		}

		String NAMATEMA;
		String NAMAUPLOADER;
		boolean DOWNLOADED;

		public String getNAMATEMA() {
			return NAMATEMA;
		}

		public void setNAMATEMA(String nAMATEMA) {
			NAMATEMA = nAMATEMA;
		}

		public String getNAMAUPLOADER() {
			return NAMAUPLOADER;
		}

		public void setNAMAUPLOADER(String nAMAUPLOADER) {
			NAMAUPLOADER = nAMAUPLOADER;
		}

		public boolean isDOWNLOADED() {
			return DOWNLOADED;
		}

		public void setDOWNLOADED(boolean dOWNLOADED) {
			DOWNLOADED = dOWNLOADED;
		}
	}

	private class OLTAdapter extends BaseAdapter {
		private Activity activity;
		private List<OLTData> data;
		LayoutInflater inflater = null;

		public OLTAdapter(Activity a, List<OLTData> d) {
			activity = a;
			data = d;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int paramInt) {
			// TODO Auto-generated method stub
			return paramInt;
		}

		@Override
		public long getItemId(int paramInt) {
			// TODO Auto-generated method stub
			return paramInt;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup paramViewGroup) {
			View vi = convertView;
			if (convertView == null) {
				vi = inflater.inflate(R.layout.ontitem, null);
			}
			TextView tv_namatema = (TextView) vi
					.findViewById(R.id.olt_namatema);
			TextView tv_namauploader = (TextView) vi
					.findViewById(R.id.olt_uploader);
			ImageView donlot = (ImageView) vi.findViewById(R.id.olt_donlot);
			OLTData d = data.get(pos);
			tv_namatema.setText("Nama Tema : " + d.getNAMATEMA());
			tv_namauploader.setText("Uploader : " + d.getNAMAUPLOADER());
			donlot.setImageDrawable(d.isDOWNLOADED() ? getActivity()
					.getResources().getDrawable(
							R.drawable.com_facebook_button_check_on)
					: getActivity().getResources().getDrawable(
							R.drawable.com_facebook_button_check_off));
			return vi;
		}

	}
}
