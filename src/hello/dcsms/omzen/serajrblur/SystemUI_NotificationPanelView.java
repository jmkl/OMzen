package hello.dcsms.omzen.serajrblur;

import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.systemui.statusbar.phone.AsusNotificationPanelView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SystemUI_NotificationPanelView {

	public static ImageView mBlurredBackground;
	public static AsusNotificationPanelView mNotificationPanelView;

	public static void hook() {

		try {

			// onFinishInflate
			XposedHelpers.findAndHookMethod(AsusNotificationPanelView.class,
					"onFinishInflate", new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {

							// guarda
							mNotificationPanelView = (AsusNotificationPanelView) param.thisObject;

							// cria um novo imageview para o blurred
							mBlurredBackground = new ImageView(
									mNotificationPanelView.getContext());
							mBlurredBackground
									.setScaleType(ScaleType.CENTER_CROP);
							mBlurredBackground.setTag("ok");

							// insere na posição 0 (antes de todas as vistas)
							FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.MATCH_PARENT);
							mNotificationPanelView.addView(mBlurredBackground,
									0, lp);

						}
					});

		} catch (Exception e) {

			XposedBridge.log(e);

		}
	}
}