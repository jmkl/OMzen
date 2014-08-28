package hello.dcsms.omzen;

import hello.dcsms.omzen.Traffic.TrafficMeter;
import hello.dcsms.omzen.Util.RLParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.os.Process;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextSwitcher;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ModStatusbar {
	public static String[] SINYAL1 = { "stat_sys_signal_null",
			"stat_sys_signal_0", "stat_sys_signal_0_fully",
			"stat_sys_signal_1", "stat_sys_signal_1_fully",
			"stat_sys_signal_2", "stat_sys_signal_2_fully",
			"stat_sys_signal_3", "stat_sys_signal_3_fully",
			"stat_sys_signal_4", "stat_sys_signal_4_fully" };
	public static String[] SINYAL2 = { "stat_sys_dual_signal_0_fully",
			"stat_sys_dual_signal_1_fully", "stat_sys_dual_signal_2_fully",
			"stat_sys_dual_signal_3_fully", "stat_sys_dual_signal_4_fully" };
	public static String[] SIM1 = { "stat_sys_dual_simcard1_n",
			"stat_sys_dual_simcard1_focus" };
	public static String[] SIM2 = { "stat_sys_dual_simcard2_n",
			"stat_sys_dual_simcard2_focus" };
	public static String[] BATERAI = { "stat_sys_battery_0",
			"stat_sys_battery_15", "stat_sys_battery_28",
			"stat_sys_battery_43", "stat_sys_battery_57",
			"stat_sys_battery_71", "stat_sys_battery_85",
			"stat_sys_battery_100" };
	public static String[] BATERAIANIM = { "stat_sys_battery_charge_anim0",
			"stat_sys_battery_charge_anim15", "stat_sys_battery_charge_anim28",
			"stat_sys_battery_charge_anim43", "stat_sys_battery_charge_anim57",
			"stat_sys_battery_charge_anim71", "stat_sys_battery_charge_anim85",
			"stat_sys_battery_charge_anim100" };
	public static String[] STAT_SYS_DATA = { "stat_sys_data_connected_1x",
			"stat_sys_data_connected_3g", "stat_sys_data_connected_4g",
			"stat_sys_data_connected_d", "stat_sys_data_connected_e",
			"stat_sys_data_connected_g", "stat_sys_data_connected_h",
			"stat_sys_data_connected_hplus", "stat_sys_data_connected_lte",
			"stat_sys_data_connected_roam", "stat_sys_data_fully_connected_1x",
			"stat_sys_data_fully_connected_3g",
			"stat_sys_data_fully_connected_4g",
			"stat_sys_data_fully_connected_d",
			"stat_sys_data_fully_connected_e",
			"stat_sys_data_fully_connected_g",
			"stat_sys_data_fully_connected_h",
			"stat_sys_data_fully_connected_hplus",
			"stat_sys_data_fully_connected_lte",
			"stat_sys_data_fully_connected_roam", "stat_sys_signal_in",
			"stat_sys_signal_inout", "stat_sys_signal_inout_null",
			"stat_sys_signal_out" };
	public static String[] PNG_SIM1 = { "sim1n", "sim1focus" };
	public static String[] PNG_SIM2 = { "sim2n", "sim2focus" };
	public static String[] PNG_SINYAL1 = { "sinyal0", "sinyal0", "sinyal0",
			"sinyal1", "sinyal1", "sinyal2", "sinyal2", "sinyal3", "sinyal3",
			"sinyal4", "sinyal4" };
	public static String[] PNG_SINYAL2 = { "sinyalnet0", "sinyalnet1",
			"sinyalnet2", "sinyalnet3", "sinyalnet4" };
	public static String[] PNG_BATERAI = { "stat_sys_battery_0",
			"stat_sys_battery_15", "stat_sys_battery_28",
			"stat_sys_battery_43", "stat_sys_battery_57",
			"stat_sys_battery_71", "stat_sys_battery_85",
			"stat_sys_battery_100" };
	public static String[] PNG_BATERAI_ANIM = {
			"stat_sys_battery_charge_anim0", "stat_sys_battery_charge_anim15",
			"stat_sys_battery_charge_anim28", "stat_sys_battery_charge_anim43",
			"stat_sys_battery_charge_anim57", "stat_sys_battery_charge_anim71",
			"stat_sys_battery_charge_anim85", "stat_sys_battery_charge_anim100" };
	private static String[] ICONLAIN = { "stat_notify_image",
			"stat_notify_image_error", "stat_sys_alarm",
			"stat_sys_data_bluetooth", "stat_sys_data_bluetooth_connected",
			"stat_sys_gps_acquiring", "stat_sys_no_sim",
			"stat_sys_ringer_silent", "stat_sys_ringer_vibrate",
			"stat_sys_signal_flightmode", "stat_sys_sync",
			"stat_sys_sync_error", "stat_sys_wifi_in", "stat_sys_wifi_inout",
			"stat_sys_wifi_inout_null", "stat_sys_wifi_out",
			"stat_sys_wifi_signal_0", "stat_sys_wifi_signal_1",
			"stat_sys_wifi_signal_1_fully", "stat_sys_wifi_signal_2",
			"stat_sys_wifi_signal_2_fully", "stat_sys_wifi_signal_3",
			"stat_sys_wifi_signal_3_fully", "stat_sys_wifi_signal_4",
			"stat_sys_wifi_signal_4_fully", "stat_sys_wifi_signal_null" };
	static ViewGroup parent;
	static ImageView sim1_num;
	static ImageView sim1_signal;
	static ImageView sim2_num;
	static ImageView sim2_signal;
	private static FrameLayout sim1_fl;
	private static FrameLayout sim2_fl;
	private static XSharedPreferences pref;
	private static String ASUS_STATBAR = "com.android.systemui.statusbar.phone.AsusPhoneStatusBar";
	private static TextSwitcher tickerswitcher;
	static TextView ticker1, ticker2, jamoriginal, battext1, battext2,
			battext3;
	private static TrafficMeter mtraffic;
	static LinearLayout layoutinout;

	public static void init(InitPackageResourcesParam resparam,
			XModuleResources modRes) {
		AturSinyalInOut(resparam);
		AturSinyalClusterLayout(resparam);
		GantiSinyalStatusBar(resparam, modRes);

	}

	static BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent i) {
			if (i.getAction().equals(S.OMZEN)) {
				android.os.Process.sendSignal(Process.myPid(),
						Process.SIGNAL_KILL);
			} else if (i.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				int style = pref.getInt(S.STATBAR_LAYOUT_STYLE, 0);
				AturUlangLayout(style);
				Intent oo = new Intent();
				oo.setAction(S.UPDATE_TRAFFIC);
				c.sendBroadcast(oo);
				AturUlangInOut();
				ModStatusbarNotif.UbahWarnaNOTIF();
			} else if (i.getAction().equals(S.ATURLAYOUT)) {
				int style = i.getIntExtra("STYLE", 0);
				AturUlangLayout(style);
			} else if (i.getAction().equals("hello.dcsms.omzen.UPDATESINYAL")) {
				AturUlangInOut();
			} else if (i.getAction()
					.equals("hello.dcsms.omzen.UPDATENOTIFTEXT")) {
				ModStatusbarNotif.UbahWarnaNOTIF();
			}

		}
	};
	static boolean sempit = true;
	private static String CLUSTER = "asus_signal_cluster_view_dual_sim";
	static LinearLayout.LayoutParams vertical = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static LinearLayout.LayoutParams horizontal = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	static LinearLayout.LayoutParams vertical05 = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
	static LinearLayout.LayoutParams horizontal05 = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
	static Context clusterContext;

	private static void AturSinyalClusterLayout(
			InitPackageResourcesParam resparam) {

		resparam.res.hookLayout(S.SYSTEMUI, "layout", CLUSTER,
				new XC_LayoutInflated() {
					android.widget.FrameLayout.LayoutParams fl_lp = new android.widget.FrameLayout.LayoutParams(
							android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
							android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
					android.widget.LinearLayout.LayoutParams ll_lp = new android.widget.LinearLayout.LayoutParams(
							android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
							android.widget.LinearLayout.LayoutParams.MATCH_PARENT);

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {
						pref = new XSharedPreferences("hello.dcsms.omzen");
						pref.makeWorldReadable();
						String device = pref.getString("NAMA_DEVICE",
								"ASUS_T00I");
						int w = 72;
						try {
							w = Integer.parseInt(pref.getString(
									"ICON_MERGE_WIDTH", "72"));

						} catch (Exception e) {
						}
						ImageView ethernet = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"ethernet", "id", S.SYSTEMUI));
						parent = (ViewGroup) ethernet.getParent();
						LinearLayout.LayoutParams lpparent = new LinearLayout.LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
								Gravity.CENTER_VERTICAL);

						clusterContext = liparam.view.getContext();
						layoutinout = new LinearLayout(liparam.view
								.getContext());
						if (device.equals("ASUS_T00F")) {
							sim1_fl = (FrameLayout) parent.getChildAt(7);
							sim2_fl = (FrameLayout) parent.getChildAt(8);

							sim1_num = (ImageView) sim1_fl.getChildAt(0);
							sim1_signal = (ImageView) sim1_fl.getChildAt(1);

							sim2_num = (ImageView) sim2_fl.getChildAt(0);
							sim2_signal = (ImageView) sim2_fl.getChildAt(1);

							parent.removeView(sim1_fl);
							parent.removeView(sim2_fl);
							layoutinout.addView(sim1_fl);
							layoutinout.addView(sim2_fl);
							parent.addView(layoutinout);

						} else {
							sim1_num = (ImageView) parent.getChildAt(7);
							sim1_signal = (ImageView) parent.getChildAt(8);
							sim2_num = (ImageView) parent.getChildAt(9);
							sim2_signal = (ImageView) parent.getChildAt(10);
							sim1_fl = new FrameLayout(liparam.view.getContext());
							sim2_fl = new FrameLayout(liparam.view.getContext());
							parent.removeView(sim1_num);
							parent.removeView(sim1_signal);
							sim1_fl.addView(sim1_num);
							sim1_fl.addView(sim1_signal);
							parent.removeView(sim2_num);
							parent.removeView(sim2_signal);
							sim2_fl.addView(sim2_num);
							sim2_fl.addView(sim2_signal);
							layoutinout.addView(sim1_fl);
							layoutinout.addView(sim2_fl);
							parent.addView(layoutinout);

						}
						layoutinout.setGravity(Gravity.CENTER_HORIZONTAL);
						sim1_num.setScaleType(ScaleType.CENTER_INSIDE);
						sim1_signal.setScaleType(ScaleType.CENTER_INSIDE);
						sim2_num.setScaleType(ScaleType.CENTER_INSIDE);
						sim2_signal.setScaleType(ScaleType.CENTER_INSIDE);

						parent.setLayoutParams(lpparent);

						// INOUT SIM 1

						LinearLayout MCombo1 = (LinearLayout) parent
								.getChildAt(4);
						ImageView MCombo1_view1 = (ImageView) MCombo1
								.getChildAt(0);
						ImageView MCombo1_view2 = (ImageView) MCombo1
								.getChildAt(1);
						MCombo1.removeView(MCombo1_view1);
						MCombo1.removeView(MCombo1_view2);
						FrameLayout Mcombo1_fl = new FrameLayout(liparam.view
								.getContext());
						MCombo1.addView(
								Mcombo1_fl,
								0,
								new LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));
						Mcombo1_fl.addView(MCombo1_view2);
						Mcombo1_fl.addView(MCombo1_view1);
						MCombo1_view2.setScaleType(ScaleType.CENTER_INSIDE);
						MCombo1_view1.setScaleType(ScaleType.CENTER_INSIDE);
						// INOUT SIM 2
						LinearLayout MCombo2 = (LinearLayout) parent
								.getChildAt(6);
						ImageView MCombo2_view1 = (ImageView) MCombo2
								.getChildAt(0);
						ImageView MCombo2_view2 = (ImageView) MCombo2
								.getChildAt(1);
						MCombo2.removeView(MCombo2_view1);
						MCombo2.removeView(MCombo2_view2);

						FrameLayout Mcombo2_fl = new FrameLayout(liparam.view
								.getContext());
						MCombo2.addView(
								Mcombo2_fl,
								0,
								new LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));
						Mcombo2_fl.addView(MCombo2_view2);
						Mcombo2_fl.addView(MCombo2_view1);
						MCombo2_view2.setScaleType(ScaleType.CENTER_INSIDE);
						MCombo2_view1.setScaleType(ScaleType.CENTER_INSIDE);

					}
				});

	}

	protected static void AturUlangInOut() {
		pref = new XSharedPreferences("hello.dcsms.omzen");
		sempit = pref.getBoolean("SMALL_SINYAL", true);
		if (sempit) {
			layoutinout.setGravity(Gravity.CENTER_HORIZONTAL);
			layoutinout.setOrientation(LinearLayout.VERTICAL);
			layoutinout.setWeightSum(1);
			layoutinout.getChildAt(0).setLayoutParams(horizontal05);
			layoutinout.getChildAt(1).setLayoutParams(horizontal05);
		} else {
			layoutinout.setGravity(Gravity.CENTER_VERTICAL);
			layoutinout.setOrientation(LinearLayout.HORIZONTAL);
			layoutinout.getChildAt(0).setLayoutParams(horizontal);
			layoutinout.getChildAt(1).setLayoutParams(horizontal);
		}

	}

	static InitPackageResourcesParam mParams;
	static XModuleResources mModres;
	static Context mContext;

	private static void GantiSinyalStatusBar(
			InitPackageResourcesParam resparam, XModuleResources modRes) {
		mParams = resparam;
		mModres = modRes;

		resparam.res.setReplacement("com.android.systemui", "drawable",
				"stat_notify_more", DrawUtils.getIcon("stat_notify_more"));

		for (int i = 0; i < STAT_SYS_DATA.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					STAT_SYS_DATA[i], DrawUtils.getIcon(STAT_SYS_DATA[i]));
		}

		for (int i = 0; i < SINYAL1.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					SINYAL1[i], DrawUtils.getIcon(PNG_SINYAL1[i]));
		}

		for (int i = 0; i < SINYAL2.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					SINYAL2[i], DrawUtils.getIcon(PNG_SINYAL2[i]));
		}

		for (int i = 0; i < SIM1.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					SIM1[i], DrawUtils.getIcon(PNG_SIM1[i]));
		}

		for (int i = 0; i < SIM2.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					SIM2[i], DrawUtils.getIcon(PNG_SIM2[i]));
		}

		for (int i = 0; i < BATERAI.length; i++) {
			XResources.setSystemWideReplacement("android", "drawable",
					BATERAI[i], DrawUtils.getIcon(PNG_BATERAI[i]));
		}

		for (int i = 0; i < BATERAIANIM.length; i++) {
			XResources.setSystemWideReplacement("android", "drawable",
					BATERAIANIM[i], DrawUtils.getIcon(PNG_BATERAI_ANIM[i]));
		}
		for (int i = 0; i < ICONLAIN.length; i++) {
			resparam.res.setReplacement("com.android.systemui", "drawable",
					ICONLAIN[i], DrawUtils.getIcon(ICONLAIN[i]));
		}
	}

	private static TextView jam;
	private static LinearLayout ll_bat;
	private static LinearLayout ll_appsicon;
	private static LinearLayout ll_sinyal;
	private static RelativeLayout main_layout;
	static LayoutParams full = new LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.MATCH_PARENT);
	static LayoutParams kiri = new LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.MATCH_PARENT);
	static LayoutParams tengah = new LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.MATCH_PARENT);
	static LayoutParams kanan = new LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.MATCH_PARENT);
	static LinearLayout SBContent;

	private static void AturSinyalInOut(final InitPackageResourcesParam respar) {
		respar.res.hookLayout(S.SYSTEMUI, "layout", "asus_status_bar",
				new XC_LayoutInflated() {

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {
						pref = new XSharedPreferences("hello.dcsms.omzen");
						pref.makeWorldReadable();
						String device = pref.getString("NAMA_DEVICE",
								"ASUS_T00I");
						int w = 72;
						try {
							w = Integer.parseInt(pref.getString(
									"ICON_MERGE_WIDTH", "72"));

						} catch (Exception e) {
						}
						mContext = liparam.view.getContext();
						SBContent = (LinearLayout) liparam.view
								.findViewById(liparam.res
										.getIdentifier("status_bar_contents",
												"id", S.SYSTEMUI));
						LinearLayout SBCluster = (LinearLayout) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"signal_battery_cluster", "id",
										S.SYSTEMUI));
						TextView bb1 = (TextView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"dock_battery_percentage", "id",
										S.SYSTEMUI));
						ImageView bb2 = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"dock_battery", "id", S.SYSTEMUI));
						ImageView bb3 = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"dock_charger", "id", S.SYSTEMUI));
						TextView bb4 = (TextView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"pad_battery_percentage", "id",
										S.SYSTEMUI));
						ImageView bb5 = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"pad_battery", "id", S.SYSTEMUI));
						ImageView bb6 = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"pad_charger", "id", S.SYSTEMUI));
						TextView bb7 = (TextView) liparam.view.findViewById(liparam.res
								.getIdentifier("battery_percentage", "id",
										S.SYSTEMUI));
						ImageView bb8 = (ImageView) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"battery", "id", S.SYSTEMUI));

						ll_appsicon = (LinearLayout) SBContent.getChildAt(0);
						ll_sinyal = (LinearLayout) SBContent.getChildAt(1);
						ll_bat = new LinearLayout(liparam.view.getContext());
						ll_bat.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
								android.view.ViewGroup.LayoutParams.MATCH_PARENT));

						jamoriginal = (TextView) ll_sinyal.getChildAt(2);

						jamoriginal.setVisibility(View.GONE);

						jam = new JamDiStatusBar(liparam.view.getContext());
						jam.setId(666);
						main_layout = new RelativeLayout(liparam.view
								.getContext());
						kiri.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						tengah.addRule(RelativeLayout.CENTER_IN_PARENT);
						kanan.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						main_layout.setLayoutParams(full);
						SBContent.addView(main_layout);
						main_layout.addView(jam, 0, tengah);

						SBCluster.removeView(bb1);
						SBCluster.removeView(bb2);
						SBCluster.removeView(bb3);
						SBCluster.removeView(bb4);
						SBCluster.removeView(bb5);
						SBCluster.removeView(bb6);
						SBCluster.removeView(bb7);
						SBCluster.removeView(bb8);

						ll_appsicon
								.setLayoutParams(new LinearLayout.LayoutParams(
										w,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));
						ll_bat.addView(bb1);
						ll_bat.addView(bb2);
						ll_bat.addView(bb3);
						ll_bat.addView(bb4);
						ll_bat.addView(bb5);
						ll_bat.addView(bb6);
						ll_bat.addView(bb7);
						ll_bat.addView(bb8);
						// ll_v1.addView(ll_bat,0);
						// 2 3 5 6 8
						bb2.setScaleType(ScaleType.CENTER_INSIDE);
						bb3.setScaleType(ScaleType.CENTER_INSIDE);
						bb5.setScaleType(ScaleType.CENTER_INSIDE);
						bb6.setScaleType(ScaleType.CENTER_INSIDE);
						bb8.setScaleType(ScaleType.CENTER_INSIDE);

						ll_bat.setGravity(Gravity.CENTER_VERTICAL);
						ll_bat.setId(111);

						battext1 = bb1;
						battext2 = bb4;
						battext3 = bb7;
						SBContent.removeView(ll_appsicon);
						SBContent.removeView(ll_sinyal);
						main_layout.addView(ll_bat, 1, kiri);
						main_layout.addView(ll_appsicon,
								RLParam.RIGHT_OF(ll_bat.getId()));
						ll_appsicon.setGravity(Gravity.CENTER_VERTICAL);
						main_layout.addView(ll_sinyal, 2, kanan);
						SBContent.setPadding(2, 0, 2, 0);

						tickerswitcher = (TextSwitcher) liparam.view
								.findViewById(liparam.res.getIdentifier(
										"tickerText", "id", S.SYSTEMUI));
						ticker1 = (TextView) tickerswitcher.getChildAt(0);
						ticker2 = (TextView) tickerswitcher.getChildAt(1);
						jam.setPadding(4, 0, 4, 0);
						ll_bat.setPadding(0, 0, 0, 0);
						ll_sinyal.setPadding(0, 0, 0, 0);
						ll_sinyal.setGravity(Gravity.CENTER_VERTICAL);
						mtraffic = new TrafficMeter(liparam.view.getContext());
						ll_sinyal.addView(mtraffic, 0);

					}
				});
	}

	private static void AturUlangLayout(int style) {
		if(SBContent!=null)
			SBContent.setBackground(DrawUtils.getDraw9(mParams,
					"statusbar_background.9"));

		pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();

		int size = 16;
		int c = Color.WHITE;
		try {
			size = Integer.parseInt(pref.getString("JAM_SIZE", "16"));
		} catch (Exception e) {
		}
		try {
			c = pref.getInt("JAM_WARNA", 0xffffff);
		} catch (Exception e) {
		}
		battext1.setTextSize(size);
		battext2.setTextSize(size);
		battext3.setTextSize(size);
		ticker1.setTextSize(size);
		ticker2.setTextSize(size);
		ticker1.setTextColor(c);
		ticker2.setTextColor(c);
		DrawUtils.setTypeFace(battext1);
		DrawUtils.setTypeFace(battext2);
		DrawUtils.setTypeFace(battext3);
		DrawUtils.setTypeFace(ticker1);
		DrawUtils.setTypeFace(ticker2);
		int w = 72;
		try {
			w = Integer.parseInt(pref.getString("ICON_MERGE_WIDTH", "72"));

		} catch (Exception e) {
		}
		switch (style) {
		case 0:
		default:
			ll_bat.setLayoutParams(kiri);
			ll_appsicon.setLayoutParams(RLParam.RIGHT_OF(ll_bat.getId(), w));
			ll_sinyal.setLayoutParams(kanan);
			jam.setLayoutParams(tengah);
			break;
		case 1:
			ll_bat.setLayoutParams(kanan);
			ll_appsicon.setLayoutParams(RLParam.LEFT_OF(ll_bat.getId(), w));
			ll_sinyal.setLayoutParams(kiri);
			jam.setLayoutParams(tengah);
			break;
		case 2:
			ll_bat.setLayoutParams(kanan);
			ll_sinyal.setLayoutParams(RLParam.LEFT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.LEFT_OF(ll_sinyal.getId(), w));
			jam.setLayoutParams(kiri);
			break;
		case 3:
			ll_bat.setLayoutParams(kiri);
			ll_sinyal.setLayoutParams(RLParam.RIGHT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.RIGHT_OF(ll_sinyal.getId(), w));
			jam.setLayoutParams(kanan);
			break;
		case 4:
			jam.setLayoutParams(kanan);
			ll_bat.setLayoutParams(RLParam.LEFT_OF(jam.getId()));
			ll_sinyal.setLayoutParams(RLParam.LEFT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.LEFT_OF(ll_sinyal.getId(), w));

			break;
		case 5:
			jam.setLayoutParams(kiri);
			ll_bat.setLayoutParams(RLParam.RIGHT_OF(jam.getId()));
			ll_sinyal.setLayoutParams(RLParam.RIGHT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.RIGHT_OF(ll_sinyal.getId(), w));
			break;
		case 6:
			jam.setLayoutParams(kanan);
			ll_bat.setLayoutParams(RLParam.LEFT_OF(jam.getId()));
			ll_sinyal.setLayoutParams(RLParam.LEFT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.KIRI(w));

			break;
		case 7:
			jam.setLayoutParams(kiri);
			ll_bat.setLayoutParams(RLParam.RIGHT_OF(jam.getId()));
			ll_sinyal.setLayoutParams(RLParam.RIGHT_OF(ll_bat.getId()));
			ll_appsicon.setLayoutParams(RLParam.KANAN(w));
			break;
		case 8:
			ll_appsicon.setLayoutParams(RLParam.TENGAH(w));
			jam.setLayoutParams(kiri);
			ll_bat.setLayoutParams(kanan);
			ll_sinyal.setLayoutParams(RLParam.LEFT_OF(ll_bat.getId()));

			break;
		case 9:
			ll_appsicon.setLayoutParams(RLParam.TENGAH(w));
			jam.setLayoutParams(kanan);
			ll_bat.setLayoutParams(kiri);
			ll_sinyal.setLayoutParams(RLParam.RIGHT_OF(ll_bat.getId()));
			break;
		case 10:
			ll_appsicon.setLayoutParams(RLParam.TENGAH(w));
			jam.setLayoutParams(kiri);
			ll_bat.setLayoutParams(kanan);
			ll_sinyal.setLayoutParams(RLParam.RIGHT_OF(jam.getId()));
			break;
		case 11:
			ll_appsicon.setLayoutParams(RLParam.TENGAH(w));
			jam.setLayoutParams(kanan);
			ll_bat.setLayoutParams(kiri);
			ll_sinyal.setLayoutParams(RLParam.LEFT_OF(jam.getId()));
			break;
		case 12:
			ll_appsicon.setLayoutParams(RLParam.TENGAH(w));
			jam.setLayoutParams(kiri);
			ll_bat.setLayoutParams(RLParam.RIGHT_OF(jam.getId()));
			ll_sinyal.setLayoutParams(kanan);
			break;
		case 13:
			break;
		case 14:
			break;
		case 15:
			break;
		case 16:
			break;
		}

		main_layout.requestLayout();
		S.log("ATURULANG : " + Integer.toString(style));
	}

	static TextView tv;

	public static void initPaket(LoadPackageParam lpparam) {

		final Class<?> phoneStatusBarClass = XposedHelpers.findClass(
				ASUS_STATBAR, lpparam.classLoader);
		XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
				"com.android.systemui.statusbar.policy.Clock",
				lpparam.classLoader), "updateClock", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				super.beforeHookedMethod(param);
				if (jamoriginal != null) {
					S.log("JAM ORI Visibility :"
							+ Integer.toString(jamoriginal.getVisibility()));
					if (jamoriginal.getVisibility() == View.VISIBLE)
						jamoriginal.setVisibility(View.GONE);
				}
			}
		});
		// XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
		// "com.android.systemui.statusbar.policy.Clock",
		// lpparam.classLoader), "updateClock", new XC_MethodHook() {
		// @Override
		// protected void beforeHookedMethod(MethodHookParam param)
		// throws Throwable {
		//
		// tv = (TextView) param.thisObject;
		// updateTextJam();
		//
		// param.setResult(null);
		// }
		// });

		XposedHelpers.findAndHookMethod(phoneStatusBarClass,
				"makeStatusBarView", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						Object mPhoneStatusBar = param.thisObject;
						Context mContext = (Context) XposedHelpers
								.getObjectField(mPhoneStatusBar, "mContext");
						IntentFilter filter = new IntentFilter();
						filter.addAction(S.OMZEN);
						filter.addAction(Intent.ACTION_BOOT_COMPLETED);
						filter.addAction(S.ATURLAYOUT);
						filter.addAction(Intent.ACTION_TIME_TICK);
						filter.addAction(Intent.ACTION_TIME_CHANGED);
						filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
						filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
						filter.addAction("hello.dcsms.omzen.UPDATEJAM");
						filter.addAction("hello.dcsms.omzen.UPDATESINYAL");
						filter.addAction("hello.dcsms.omzen.UPDATENOTIFTEXT");
						mContext.registerReceiver(receiver, filter);
						pref = new XSharedPreferences("hello.dcsms.omzen");
						pref.makeWorldReadable();
						int style = pref.getInt("STATUSBAR_LAYOUT", 0);
						AturUlangLayout(style);
						Intent oo = new Intent();
						oo.setAction(S.UPDATEJAM);
						mContext.sendBroadcast(oo);
						AturUlangInOut();
						ModStatusbarNotif.UbahWarnaNOTIF();
					}
				});

	}

	public static String PKGNAME = "hello.dcsms.omzen";

	protected static void updateTextJam() {
		if (tv == null)
			return;

		pref = new XSharedPreferences(PKGNAME);
		pref.makeWorldReadable();
		Date now = new Date();
		boolean uppercase = false;
		String txt = pref.getString("FORMAT_JAM", "^E, HH:mm");
		uppercase = txt.contains("^") ? true : false;
		String ok = txt;
		if (uppercase) {
			ok = ok.replace("^", "");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(ok, Locale.US);
		String currentDateandTime = sdf.format(now);
		if (uppercase) {
			currentDateandTime = currentDateandTime.toUpperCase(Locale.US);
		}

		tv.setText(currentDateandTime);
		int c = Color.WHITE;
		try {
			c = pref.getInt("JAM_WARNA", 0xffffff);

		} catch (Exception e) {

		}
		tv.setTextColor(c);
		tv.setShadowLayer(1, 1, 1, Color.BLACK);

	}

	public static void initZygot(XSharedPreferences pref2) {
		pref = pref2;

	}

}
