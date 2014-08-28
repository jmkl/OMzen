package hello.dcsms.omzen;

import hello.dcsms.omzen.R;
import hello.dcsms.omzen.PowerMenu.ModPowerMenu;
import hello.dcsms.omzen.serajrblur.SystemUI_BaseStatusBar;
import hello.dcsms.omzen.serajrblur.SystemUI_NotificationPanelView;
import hello.dcsms.omzen.serajrblur.SystemUI_PanelView;
import hello.dcsms.omzen.serajrblur.SystemUI_PhoneStatusBar;

import java.lang.reflect.Method;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Modul implements IXposedHookZygoteInit, IXposedHookLoadPackage,
		IXposedHookInitPackageResources {
	private String MOD_PATH;
	public static String ANDROID_PACKAGE_NAME = "android";
	public static String SYSTEM_UI_PACKAGE_NAME = "com.android.systemui";
	private static String mModulePath;
	private static ClassLoader mClassLoader;
	private static XSharedPreferences mXSharedPreferences;
	private static XModuleResources mXModuleResources;
	private static InitPackageResourcesParam mInitPackageResourcesParam;

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam)
			throws Throwable {

		mInitPackageResourcesParam = resparam;
		mXModuleResources = XModuleResources.createInstance(mModulePath,
				resparam.res);
		if (resparam.packageName.equals("com.bbm")) {
			// ic_notification
			resparam.res.setReplacement("com.bbm", "drawable",
					"ic_notification", DrawUtils.getIcon("com_bbm_notif_icon"));
			resparam.res.setReplacement("com.bbm", "drawable",
					"ic_notification_splat",
					DrawUtils.getIcon("com_bbm_notif_icon_splat"));

		}

		else if (resparam.packageName.equals(S.SYSTEMUI)) {
			XposedBridge.log("HandleInitPackage : SystemUI");
			try {
				XModuleResources modRes = XModuleResources.createInstance(
						MOD_PATH, resparam.res);
				ModStatusbar.init(resparam, modRes);
				boolean notif = pref.getBoolean("OMNOTIFIKASI", false);
				if (notif) {

					ModStatusbarNotif.init(resparam);
				}

				// asus_ep_btn_bg_on
				// asus_ep_btn_bg_off
				// asus_ep_btn_bg_on
				// asus_ep_btn_bg_hl

				// resparam.res
				// .setReplacement(
				// S.SYSTEMUI,
				// "drawable",
				// "asus_phone_control_onoff_vibrate",
				// DrawUtils
				// .getLayerDrawable("asus_ep_statusicon_vibrate"));
				// resparam.res
				// .setReplacement(
				// S.SYSTEMUI,
				// "drawable",
				// "asus_phone_control_onoff_mute",
				// DrawUtils
				// .getLayerDrawable("asus_ep_statusicon_silent"));
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_control_onoff_volume",
				// DrawUtils.getLayerDrawable("asus_ep_statusicon_sound"));
				// resparam.res
				// .setReplacement(
				// S.SYSTEMUI,
				// "drawable",
				// "asus_phone_control_onoff_silent",
				// DrawUtils
				// .getLayerDrawable("asus_ep_statusicon_silent"));

				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_ep_btn_bg_on", DrawUtils.getColorDrawable(aktif));
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_ep_btn_bg_hl", DrawUtils.getColorDrawable(press));

				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_quickbox_onoff",
				// DrawUtils.stateDrawable());
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_control_wifi_pressed",
				// DrawUtils.stateDrawable());
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_control_onoff",
				// DrawUtils.stateDrawable());
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_control_onoff2",
				// DrawUtils.stateDrawable());
				// resparam.res.setReplacement(S.SYSTEMUI, "drawable",
				// "asus_phone_memory_onoff_green",
				// DrawUtils.stateDrawable());

				resparam.res.setReplacement(S.SYSTEMUI, "drawable",
						"asus_phone_control_onoff_bg",
						modRes.fwd(R.drawable.asus_phone_control_onoff_bg));

			} catch (Exception e) {
				S.log(e.getMessage());
			}
		
		}
		;// mXModuleResources.fwd(R.drawable.asus_phone_quickbox_text_color));

	}

	static XSharedPreferences pref;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		mClassLoader = lpparam.classLoader;

		if (lpparam.packageName.equals(S.SYSTEMUI)) {
			XposedBridge.log("HandleLoadPackage : SystemUI");
			// setam os class loaderes parentes
			setParentClassLoaders(lpparam);
			// recarregam as preferências
			mXSharedPreferences.reload();
			ModStatusbar.initPaket(lpparam);

			// hooks
			pref = new XSharedPreferences("hello.dcsms.omzen");
			pref.makeWorldReadable();
			boolean notif = pref.getBoolean("OMNOTIFIKASI", false);
			if (notif) {
				ModStatusbarNotif.initPaket(lpparam);
			}
			boolean blur = pref.getBoolean("BLUR_NOTIFIKASI", false);
			if (blur) {
				SystemUI_PhoneStatusBar.hook();
				SystemUI_BaseStatusBar.hook();
				SystemUI_PanelView.hook();
				SystemUI_NotificationPanelView.hook();
			}
			try {
				Method SS = Class.forName(
						"com.android.systemui.screenshot.GlobalScreenshot",
						false, lpparam.classLoader).getDeclaredMethod(
						"saveScreenshotInWorkerThread", Runnable.class);
				// XposedBridge.hookMethod(SS, new XC_MethodHook() {
				// @Override
				// protected void beforeHookedMethod(MethodHookParam param)
				// throws Throwable {
				// Bitmap mScreenBitmap = (Bitmap) XposedHelpers
				// .getObjectField(param.thisObject,
				// "mScreenBitmap");
				// Bitmap ssbg = BitmapFactory.decodeFile(Environment
				// .getExternalStorageDirectory()
				// .getAbsolutePath()
				// + "/ssbg.jpg");
				// // 77 197
				// if (mScreenBitmap != null) {
				// XposedBridge.log(Integer.toString(mScreenBitmap
				// .getWidth()));
				// Bitmap okess = Bitmap.createBitmap(ssbg.getWidth(),
				// ssbg.getHeight(), Config.ARGB_8888);
				// Canvas c = new Canvas(okess);
				// Paint p = new Paint();
				// c.drawBitmap(ssbg, 0, 0, p);
				// c.drawBitmap(mScreenBitmap, 77, 197, p);
				// mScreenBitmap = okess;
				// }
				// }
				// });

				Method cekOverFlowIcon = Class.forName(
						"com.android.systemui.statusbar.phone.IconMerger",
						false, lpparam.classLoader).getDeclaredMethod(
						"checkOverflow", int.class);

				XposedBridge.hookMethod(cekOverFlowIcon, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(final MethodHookParam param)
							throws Throwable {
						LinearLayout par = (LinearLayout) param.thisObject;
						pref = new XSharedPreferences("hello.dcsms.omzen");
						pref.makeWorldReadable();
						int style = pref.getInt("STATUSBAR_LAYOUT", 0);
						switch (style) {
						default:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.LEFT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 1:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.RIGHT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

							break;
						case 2:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.RIGHT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

							break;

						case 3:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.LEFT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 4:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.RIGHT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
							break;
						case 5:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.LEFT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 6:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.LEFT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;

						case 7:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.RIGHT);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
							break;

						case 8:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.CENTER);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 9:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.CENTER);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 10:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.CENTER);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 11:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.CENTER);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
							break;
						case 12:
							((LinearLayout) param.thisObject)
									.setGravity(Gravity.CENTER);
							par.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

							break;

						}

						((View) param.thisObject).requestLayout();

					}
				});

			} catch (Exception e) {
				S.log(e.getMessage());
			}
		}
		if (lpparam.packageName.equals(ModPowerMenu.PACKAGE_NAME)) {
			pref = new XSharedPreferences(PKGNAME);
			pref.makeWorldReadable();
			boolean boo = pref.getBoolean("MOD_POWER_MENU", false);
			if (boo) {
				ModPowerMenu.init(pref,lpparam.classLoader);
			}
		}

	}

	private void setParentClassLoaders(LoadPackageParam lpparam) {
		// todos os classloaders
		ClassLoader packge = lpparam.classLoader;
		ClassLoader module = getClass().getClassLoader();
		ClassLoader xposed = module.getParent();
		XposedHelpers.setObjectField(packge, "parent", xposed);
		XposedHelpers.setObjectField(module, "parent", packge);

	}

	public static String PKGNAME = "hello.dcsms.omzen";

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mModulePath = startupParam.modulePath;
		mXSharedPreferences = new XSharedPreferences(PKGNAME);
		MOD_PATH = startupParam.modulePath;
		pref = new XSharedPreferences(PKGNAME);
		pref.makeWorldReadable();
		ModStatusbar.initZygot(pref);
		XResources.setSystemWideReplacement("android", "dimen",
				"status_bar_height", 25);

	}

	public static String getXposedModulePath() {

		return mModulePath;

	}

	public static XModuleResources getXposedModuleResources() {

		return mXModuleResources;

	}

	public static InitPackageResourcesParam getXposedInitPackageResourcesParam() {

		return mInitPackageResourcesParam;

	}

	public static XSharedPreferences getXposedXSharedPreferences() {

		return mXSharedPreferences;

	}

}
