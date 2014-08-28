package hello.dcsms.omzen;

import java.lang.reflect.Method;

import android.util.DisplayMetrics;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ModBlurNotif {

	public static void init(LoadPackageParam lpparam) {
		Method cekOverFlowIcon;
		try {
			cekOverFlowIcon = Class
					.forName(
							"com.android.systemui.statusbar.phone.AsusNotificationPanelView",
							false, lpparam.classLoader).getDeclaredMethod(
							"onFinishInflate");

			XposedBridge.hookMethod(cekOverFlowIcon, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param)
						throws Throwable {
					S.log("Hook NotifPanel");

					DisplayMetrics localDisplayMetrics = new DisplayMetrics();
					// Bitmap localObject =
					// SurfaceControl.screenshot(localDisplayMetrics.widthPixels,localDisplayMetrics.heightPixels);

					// S.log(Integer.toString(localObject.getWidth()));
				}
			});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
