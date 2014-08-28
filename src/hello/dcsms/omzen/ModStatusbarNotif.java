package hello.dcsms.omzen;

import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.systemui.statusbar.phone.AsusQuickSettings;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ModStatusbarNotif {
	private static String[] TOMBOLTIGA = { "asus_ep_btn_edit_hl",
			"asus_ep_btn_edit_n", "asus_ep_btn_list_hl", "asus_ep_btn_list_n",
			"asus_ep_btn_quicksetting_hl", "asus_ep_btn_quicksetting_n",
			"asus_ep_btn_setting_hl", "asus_ep_btn_setting_n" };
	private static String[] TOGGLE_ICON = { "asus_ep_statusicon_always_off",
			"asus_ep_statusicon_always_on",
			"asus_ep_statusicon_audio_wizard_on",
			"asus_ep_statusicon_autosync_off",
			"asus_ep_statusicon_autosync_on",
			"asus_ep_statusicon_auto_rotate_off",
			"asus_ep_statusicon_auto_rotate_on",
			"asus_ep_statusicon_bluetooth_off",
			"asus_ep_statusicon_bluetooth_on",
			"asus_ep_statusicon_calculator_on", "asus_ep_statusicon_camera_on",
			"asus_ep_statusicon_clean", "asus_ep_statusicon_data_off",
			"asus_ep_statusicon_data_on", "asus_ep_statusicon_dictionary_off",
			"asus_ep_statusicon_dictionary_on",
			"asus_ep_statusicon_flashlight_off",
			"asus_ep_statusicon_flashlight_on",
			"asus_ep_statusicon_flightmode_off",
			"asus_ep_statusicon_flightmode_on",
			"asus_ep_statusicon_fmradio_on", "asus_ep_statusicon_gps_off",
			"asus_ep_statusicon_gps_on",
			"asus_ep_statusicon_miracast_setting_off",
			"asus_ep_statusicon_miracast_setting_on",
			"asus_ep_statusicon_nfc_off", "asus_ep_statusicon_nfc_on",
			"asus_ep_statusicon_pavingnew_off",
			"asus_ep_statusicon_pavingnew_on",
			"asus_ep_statusicon_quickmemo_on",
			"asus_ep_statusicon_reading_off", "asus_ep_statusicon_reading_on",
			"asus_ep_statusicon_silent", "asus_ep_statusicon_silent_off",
			"asus_ep_statusicon_sim1_off", "asus_ep_statusicon_sim1_on",
			"asus_ep_statusicon_sim2_off", "asus_ep_statusicon_sim2_on",
			"asus_ep_statusicon_sound", "asus_ep_statusicon_vibrate",
			"asus_ep_statusicon_voice_recorder_on",
			"asus_ep_statusicon_wifisharing_off",
			"asus_ep_statusicon_wifisharing_on", "asus_ep_statusicon_wifi_off",
			"asus_ep_statusicon_wifi_on", "battery_low_battery" };

	private static String[] TOGLEBUTTON = { "asus_phone_control_autorotate_bg",
			"asus_phone_control_autosync_bg",
			"asus_phone_control_bluetooth_bg",
			"asus_phone_control_flightmode_bg", "asus_phone_control_gps_bg",
			"asus_phone_control_miracast_bg",
			"asus_phone_control_mobiledata_bg",
			"asus_phone_control_powersaving2_bg",
			"asus_phone_control_silent_bg", "asus_phone_control_wifi_bg",
			"asus_phone_control_wifisharing_bg", "vibrate", "mute", "sound",
			"silent", "asus_phone_quickbox_audio",
			"asus_phone_quickbox_calculator",
			"asus_phone_quickbox_clean_memory_green",
			"asus_phone_quickbox_clean_memory_red",
			"asus_phone_quickbox_clean_memory_yellow",
			"asus_phone_quickbox_soundrecord" };
	private static String[] TOGLEXMLBUTTON = {
			"asus_phone_control_onoff_autorotate",
			"asus_phone_control_onoff_autosync",
			"asus_phone_control_onoff_bluetooth",
			"asus_phone_control_onoff_flightmode",
			"asus_phone_control_onoff_gps",
			"asus_phone_control_onoff_miracast",
			"asus_phone_control_onoff_mobiledata",
			"asus_phone_control_onoff_powersaving2",
			"asus_phone_control_onoff_silent",
			"asus_phone_control_onoff_wifi",
			"asus_phone_control_onoff_wifisharing",
			"asus_phone_control_onoff_vibrate",
			"asus_phone_control_onoff_mute",
			"asus_phone_control_onoff_volume",
			"asus_phone_control_onoff_silent",
			// tambahan
			"asus_phone_quickbox_audio", "asus_phone_quickbox_calculator",
			"asus_phone_quickbox_clean_memory_green",
			"asus_phone_quickbox_clean_memory_red",
			"asus_phone_quickbox_clean_memory_yellow",
			"asus_phone_quickbox_soundrecord" };
	//
	static InitPackageResourcesParam rParam;

	public static void init(final InitPackageResourcesParam resparam) {
		try {
			resparam.res.hookLayout("com.android.systemui", "layout",
					"status_bar_quick_settings_brightness_phone",
					new XC_LayoutInflated() {

						@Override
						public void handleLayoutInflated(
								LayoutInflatedParam liparam) throws Throwable {
							SeekBar sb = (SeekBar) liparam.view
									.findViewById(liparam.res.getIdentifier(
											"slider", "id",
											"com.android.systemui"));
							sb.setLayoutParams(new LayoutParams(15,
									LayoutParams.MATCH_PARENT));
							LayoutParams l = sb.getLayoutParams();
							int w = l.width;
							S.log_i(w);

						}
					});
			rParam = resparam;

			gantiwarnatogel(resparam);
			HookNotifikasiKolor(resparam);
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_quickbox_text_color",
					DrawUtils.getColorStateList());
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_control_onoff_text_color",
					DrawUtils.getColorStateList());
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_control_onoff_text_color2",
					DrawUtils.getColorStateList());

			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_memory_text_color_green",
					DrawUtils.getColorStateList());
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_memory_text_color_red",
					DrawUtils.getColorStateList());
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_phone_memory_text_color_yellow",
					DrawUtils.getColorStateList());

			for (int i = 0; i < TOGLEXMLBUTTON.length; i++) {
				resparam.res.setReplacement("com.android.systemui", "drawable",
						TOGLEXMLBUTTON[i],
						DrawUtils.getToggleDrawableState(TOGLEBUTTON[i]));
			}
			for (int i = 0; i < TOMBOLTIGA.length; i++) {
				resparam.res.setReplacement("com.android.systemui", "drawable",
						TOMBOLTIGA[i], DrawUtils.getIcon(TOMBOLTIGA[i]));
			}
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"ic_notify_clear", DrawUtils.getClearButton(resparam));
			XSharedPreferences pref = new XSharedPreferences(
					"hello.dcsms.omzen");
			pref.makeWorldReadable();
			boolean blur = pref.getBoolean("BLUR_NOTIFIKASI", true);
			if (blur) {
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"system_bar_background", DrawUtils.Trans());
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"panel_background", DrawUtils.Trans());
				resparam.res
						.setReplacement("com.android.systemui", "drawable",
								"asus_notification_panel_background",
								DrawUtils.Trans());
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"asus_notification_header_background",
						DrawUtils.Trans());
				XResources.setSystemWideReplacement("android", "drawable",
						"notification_bg", DrawUtils.Trans());
				XResources.setSystemWideReplacement("android", "drawable",
						"notification_bg_low", DrawUtils.Trans());
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"asus_handle_bar_background", DrawUtils.Trans());
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"status_bar_close", DrawUtils.Trans());
			} else {
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"system_bar_background", DrawUtils.getDrawableNine(
								resparam, "statusbar_background.9"));
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"panel_background",
						DrawUtils.getDrawableNine(resparam, "panel_bg.9"));
				resparam.res
						.setReplacement("com.android.systemui", "drawable",
								"asus_notification_panel_background",
								DrawUtils.getDrawableNine(resparam,
										"asus_notification_panel_background.9"));
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"asus_notification_header_background",
						DrawUtils.getDrawableNine(resparam,
								"asus_notification_header_background.9"));
				XResources.setSystemWideReplacement("android", "drawable",
						"notification_bg", DrawUtils.getDrawableNine(resparam,
								"notification_bg.9"));
				XResources.setSystemWideReplacement("android", "drawable",
						"notification_bg_low", DrawUtils.getDrawableNine(
								resparam, "notification_bg_low.9"));
				resparam.res
						.setReplacement("com.android.systemui", "drawable",
								"asus_handle_bar_background", DrawUtils
										.getDrawableNine(resparam,
												"panel_handle_bg.9"));
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"status_bar_close",
						DrawUtils.getIcon("asus_ep_pulldown_bar"));

			}

			// seekbar
			resparam.res
					.setReplacement("com.android.systemui", "drawable",
							"asus_brightness_bg", DrawUtils.getDrawableNine(
									resparam, "asus_brightness_bg.9"));
			resparam.res.setReplacement("com.android.systemui", "drawable",
					"asus_quicksetting_brightness_slider",
					DrawUtils.getSeekbarDrawable(resparam));
			UbahWarnaNOTIF();

		} catch (Exception e) {
			S.log(e.getMessage());
		}

	}

	static TextView notif_clock, notif_date,CarrierText,WifiText;
	static FrameLayout tognotif_parent;
	static XSharedPreferences pref;

	private static void HookNotifikasiKolor(InitPackageResourcesParam resparam) {
		resparam.res.hookLayout("com.android.systemui", "layout",
				"asus_status_bar_expanded", new XC_LayoutInflated() {

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {
					
						CarrierText =  (TextView) liparam.view.findViewById(liparam.res
								.getIdentifier("asus_sim_carrier_label", "id",
										"com.android.systemui"));
						WifiText =  (TextView) liparam.view.findViewById(liparam.res
								.getIdentifier("asus_wifi_label", "id",
										"com.android.systemui"));
						
					}
				});
		resparam.res.hookLayout("com.android.systemui", "layout",
				"asus_status_bar_expanded_header", new XC_LayoutInflated() {

					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam)
							throws Throwable {
						RelativeLayout rl = (RelativeLayout) liparam.view.findViewById(liparam.res
								.getIdentifier("header", "id",
										"com.android.systemui"));
						notif_clock = (TextView) rl.getChildAt(0);
						notif_date = (TextView) rl.getChildAt(1);
						tognotif_parent = (FrameLayout) rl.getChildAt(8);
						notif_clock.setTextColor(Color.WHITE);
						notif_date.setTextColor(Color.WHITE);
						((TextView) tognotif_parent.getChildAt(0))
								.setTextColor(Color.WHITE);
						((TextView) tognotif_parent.getChildAt(1))
								.setTextColor(Color.WHITE);
					}
				});

	}

	public static void UbahWarnaNOTIF() {
		pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();
		int jam = pref.getInt("WARNA_JAM_NOTIF", Color.WHITE);
		int qs = pref.getInt("WARNA_QS_NOTIF", Color.WHITE);
		int carrier = pref.getInt("WARNA_CARRIERNWIFI", Color.WHITE);
		if (notif_clock != null && notif_date != null
				&& tognotif_parent != null) {
			notif_clock.setTextColor(jam);
			notif_date.setTextColor(jam);
			((TextView) tognotif_parent.getChildAt(0)).setTextColor(qs);
			((TextView) tognotif_parent.getChildAt(1)).setTextColor(qs);
		}
		if(CarrierText!=null)
			CarrierText.setTextColor(carrier);
		if(WifiText!=null)
			WifiText.setTextColor(carrier);


	}

	private static void gantiwarnatogel(InitPackageResourcesParam resparam) {
		if (resparam == null)
			return;

	}

	public static final String USERICON = "com.android.systemui.statusbar.policy.QuickSettingsUserInfoController";
	static TextView mText;
	private static ViewGroup mQuickboxContainerView;
	private static ViewGroup mQuicksettingContainerView;

	public static void initPaket(final LoadPackageParam lpparam) {

		XposedBridge.hookAllConstructors(AsusQuickSettings.class,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						super.afterHookedMethod(param);
						mQuickboxContainerView = (ViewGroup) XposedHelpers.getObjectField(
								param.thisObject, "mQuickboxContainerView");
						mQuicksettingContainerView = (ViewGroup) XposedHelpers.getObjectField(
								param.thisObject, "mQuicksettingContainerView");
						
					
					}
				});
		if(mQuicksettingContainerView!=null){
			S.log(Integer.toString(mQuickboxContainerView.getChildCount()));
		}
		if(mQuicksettingContainerView!=null){
			S.log(Integer.toString(mQuicksettingContainerView.getChildCount()));
		}

		final Class<?> userinfo = XposedHelpers.findClass(USERICON,
				lpparam.classLoader);

		XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
				"com.android.systemui.statusbar.phone.AsusControllerCheckbox",
				lpparam.classLoader), "onMeasure", int.class, int.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						super.beforeHookedMethod(param);

					}

					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						super.afterHookedMethod(param);
						int x = (Integer) param.args[1];

						S.log("ONMESUARE " + Integer.toString(x));
					}

				});
		XposedHelpers.findAndHookMethod(userinfo, "getRoundedCornerBitmap",
				Bitmap.class, float.class, int.class, new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						if (param.args[0] == null)
							return;
						Bitmap bx = (Bitmap) param.args[0];//
						Bitmap b = Bitmap.createScaledBitmap(bx, 90, 90, true);
						Bitmap ok = Bitmap.createBitmap(90, 90,
								Config.ARGB_8888);
						Bitmap mask = sc(BitmapFactory.decodeFile(DrawUtils
								.getIconPath("toggle_btn_bg_mask")));
						Bitmap top = sc(BitmapFactory.decodeFile(DrawUtils
								.getIconPath("toggle_btn_bg_top")));
						Canvas c = new Canvas(ok);
						Paint p = new Paint();
						c.drawBitmap(mask, 0, 0, p);
						p.setXfermode(new PorterDuffXfermode(
								android.graphics.PorterDuff.Mode.SRC_IN));
						c.drawBitmap(b, 0, 0, p);
						p.setXfermode(null);
						c.drawBitmap(top, 0, 0, p);
						param.setResult(ok);
					}
				});

	}

	private static Bitmap sc(Bitmap src) {
		return Bitmap.createScaledBitmap(src, 90, 90, true);
	}
}
