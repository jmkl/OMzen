package hello.dcsms.omzen.serajrblur;

import hello.dcsms.omzen.Modul;
import hello.dcsms.omzen.R;

import java.util.ArrayList;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.statusbar.BaseStatusBar;
import com.android.systemui.statusbar.NotificationData;
import com.android.systemui.statusbar.NotificationData.Entry;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SystemUI_BaseStatusBar {

	public static boolean mTranslucentNotifications;
	protected static NotificationData mNotificationData;
	private static Drawable mTranslucentNotificationsFocusDrawable;

	public static void hook() {

		try {

			// constructor
			XposedBridge.hookAllConstructors(BaseStatusBar.class,
					new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {

							// guarda
							mNotificationData = (NotificationData) XposedHelpers
									.getObjectField(param.thisObject,
											"mNotificationData");

							// guarda o drawable
							mTranslucentNotificationsFocusDrawable = Modul
									.getXposedModuleResources()
									.getDrawable(
											R.drawable.status_bar_item_background_focus);

						}
					});

			// inflateViews
			XposedHelpers.findAndHookMethod(BaseStatusBar.class,
					"inflateViews", Entry.class, ViewGroup.class,
					new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {

							// notificações transparentes ?
							if (!mTranslucentNotifications)
								return;

							// obtém os dados da notificação
							Entry entry = (Entry) param.args[0];

							// seta o fundo transparente
							setTranslucentNotificationBackground(entry);

						}
					});

		} catch (Exception e) {

			XposedBridge.log(e);

		}
	}

	public static void updatePreferences(XSharedPreferences prefs) {

		// atualiza
		mTranslucentNotifications = true;// prefs.getBoolean(BlurSettings_Fragment.TRANSLUCENT_NOTIFICATIONS_PREFERENCE_KEY,
											// BlurSettings_Fragment.TRANSLUCENT_NOTIFICATIONS_PREFERENCE_DEFAULT);

		// atualiza tb as notificações visíveis (se necessário)
		updateVisibleNotificationsBackground();

	}

	private static StateListDrawable getTranslucentNotificationsBackground() {

		// cria o StateListDrawable baseado no notification_bg.xml
		// do framework-res.apk, mas com os drawables do módulo !!
		StateListDrawable bg = new StateListDrawable();
		bg.setExitFadeDuration(android.R.integer.config_mediumAnimTime);

		// normal
		bg.addState(new int[] { -android.R.attr.state_pressed },
				new ColorDrawable(Color.TRANSPARENT));

		// pressionado
		bg.addState(new int[] { android.R.attr.state_pressed },
				new ColorDrawable(Color.argb(50, 255, 255, 255)));

		// focado
		bg.addState(new int[] { android.R.attr.state_focused,
				-android.R.attr.state_pressed },
				mTranslucentNotificationsFocusDrawable);

		return bg;

	}

	private static void updateVisibleNotificationsBackground() {

		// continua ?
		if (mNotificationData == null)
			return;

		// passa por todas as notificações visíveis
		for (int i = 0; i < mNotificationData.size(); i++) {

			// obtém os dados da notificação
			Entry entry = mNotificationData.get(i);

			// seta o fundo transparente
			setTranslucentNotificationBackground(entry);

		}
	}

	private static void setTranslucentNotificationBackground(Entry entry) {

		// content
		if (entry.content != null) {
			setTranslucentNotificationBackground(entry.content);
		}

		// expanded
		if (entry.expanded != null) {
			setTranslucentNotificationBackground(entry.expanded);
		}

		// expandedLarge
		View expandedLarge = (View) XposedHelpers.callMethod(entry,
				"getLargeView");
		if (expandedLarge != null) {
			setTranslucentNotificationBackground(expandedLarge);
		}

	}

	private static void setTranslucentNotificationBackground(View entryView) {

		// notificações transparentes ?
		if (mTranslucentNotifications) {

			// obtém o resources
			Resources res = entryView.getResources();

			// obtém todos os views dessa notificação
			ArrayList<View> children = Utils.getAllChildrenViews(entryView);
			for (View child : children) {

				// continua ?
				if (child != null) {

					// obté, o id
					int resId = child.getId();

					// id válido ?
					if (resId != 0) {

						try {

							// acha o nome do id
							String nameResIs = resId != 0 ? res
									.getResourceEntryName(resId) : "";

							// existe um background ?
							if (child.getBackground() != null) {

								// Log.d("child_id_name", nameResIs + " | " +
								// child.getClass().toString());

								// despresa se se for um desses id's...
								if (nameResIs.contains("icon")
										|| nameResIs.contains("glow")
										|| nameResIs.contains("divider")) {
									continue;
								}

								// remove o fundo e limpa o cache
								child.setBackground(null);
								child.destroyDrawingCache();

								// resedenha
								child.invalidate();

							}

						} catch (NotFoundException e) {

							// erro !!!
							e.printStackTrace();
							continue;

						}
					}
				}
			}

			// seta o background transparente
			entryView.setBackground(getTranslucentNotificationsBackground());
			entryView.setSelected(false);

		} else {

			// seta o background padrão do framework
			entryView
					.setBackgroundResource(com.android.internal.R.drawable.notification_bg);
			entryView.setSelected(false);

		}

		// resedenha
		entryView.invalidate();

	}
}