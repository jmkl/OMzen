package hello.dcsms.omzen.serajrblur;

import hello.dcsms.omzen.Modul;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.systemui.statusbar.phone.AsusPhoneStatusBar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SystemUI_PhoneStatusBar {

	private static Context mContext;
	private static Resources mResources;
	private static int mCloseHandleHeight;
	private static boolean mBlurredNotificationPanelEnabled;
	private static int mBlurredNotificationPanelScale;
	private static int mBlurredNotificationPanelRadius;
	private static int mBlurredNotificationPanelColor;
	private static boolean mAdjustmentsStartMarginPortrait;
	private static boolean mAdjustmentsStartMarginLandscape;

	public static void hook() {

		try {

			// makeStatusBarView
			XposedHelpers.findAndHookMethod(AsusPhoneStatusBar.class,
					"makeStatusBarView", new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(
								final MethodHookParam param) throws Throwable {

							// obtém os campos
							mContext = (Context) XposedHelpers.getObjectField(
									param.thisObject, "mContext");
							mResources = mContext.getResources();

							// dimensões
							mCloseHandleHeight = mResources.getDimensionPixelSize(mResources
									.getIdentifier("close_handle_height",
											"dimen",
											Modul.SYSTEM_UI_PACKAGE_NAME));

							// inicia o render script
							if (Utils.getAndroidAPILevel() >= 17) {
								Utils.Blur.initRenderScript(mContext);
							}

							// receiver
							BroadcastReceiver br = new BroadcastReceiver() {

								@Override
								public void onReceive(Context context,
										Intent intent) {

									String action = intent.getAction();
									Handler handler = new Handler();

									// -----------------------------------------------------------------------
									// se na rotação do celular o mod estiver
									// habilitado e o painel expandido
									// estiver aberto, fecha o painel expandido,
									// forçando o usuário a expandir
									// o painel novamente para obtér a imagem
									// desfocada com a rotação atual !!
									// -----------------------------------------------------------------------
									if (action
											.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {

										// obtém os campos
										boolean mExpandedVisible = XposedHelpers
												.getBooleanField(
														param.thisObject,
														"mExpandedVisible");

										// habilitado ?
										if (mBlurredNotificationPanelEnabled
												&& mExpandedVisible) {

											// fecha o painel
											XposedHelpers.callMethod(
													param.thisObject,
													"makeExpandedInvisible");

										}
									}

								}
							};

							// registra o receiver
							IntentFilter intent = new IntentFilter();
							intent.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
							mContext.registerReceiver(br, intent);

							// atualizam as preferências
							updatePreferences();

						}
					});

			// makeExpandedInvisible
			XposedBridge.hookMethod(
					Utils.getAndroidAPILevel() >= 19
					// >= 4.4
					? XposedHelpers.findMethodExact(AsusPhoneStatusBar.class,
							"makeExpandedVisible")
					// <= 4.3
							: XposedHelpers.findMethodExact(
									AsusPhoneStatusBar.class,
									"makeExpandedVisible"),
					new XC_MethodHook() {

						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {

							// habilitado ?
							if (mBlurredNotificationPanelEnabled) {

								int left = mCloseHandleHeight;
								int top = mCloseHandleHeight;

								if (mResources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

									left = 0;

									// não utilizar o padding ?
									if (!mAdjustmentsStartMarginPortrait) {
										top = 0;
									}

								} else if (mResources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

									top = 0;

									// não utilizar o padding ?
									if (!mAdjustmentsStartMarginLandscape) {
										left = 0;
									}

								}

								// seta o padding da ImageView de acordo com a
								// rotação e escolha do usuário
								SystemUI_NotificationPanelView.mBlurredBackground
										.setPadding(left, top, 0, 0);

								// blur
								new BlurTask()
										.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							}
						}
					});

			// makeExpandedInvisible
			XposedHelpers.findAndHookMethod(AsusPhoneStatusBar.class,
					"makeExpandedInvisible", new XC_MethodHook() {

						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {

							// limpa a imageview e a memória utilizada
							if (SystemUI_NotificationPanelView.mBlurredBackground != null
									&& SystemUI_NotificationPanelView.mBlurredBackground
											.getDrawable() != null) {

								// bitmap ?
								if (SystemUI_NotificationPanelView.mBlurredBackground
										.getDrawable() instanceof BitmapDrawable) {

									// recicla
									Bitmap bitmap = ((BitmapDrawable) SystemUI_NotificationPanelView.mBlurredBackground
											.getDrawable()).getBitmap();
									bitmap.recycle();
									bitmap = null;

								}

								// limpa
								SystemUI_NotificationPanelView.mBlurredBackground
										.setImageDrawable(null);

							}
						}
					});

		} catch (Exception e) {

			XposedBridge.log(e);

		}
	}

	public static String BLUR_ENABLED_PREFERENCE_KEY = "hook_system_ui_blurred_expanded_panel_enabled_pref";
	public static boolean BLUR_ENABLED_PREFERENCE_DEFAULT = true;

	public static String BLUR_SCALE_PREFERENCE_KEY = "hook_system_ui_blurred_expanded_panel_scale_pref";
	public static String BLUR_SCALE_PREFERENCE_DEFAULT = "20";

	public static String BLUR_RADIUS_PREFERENCE_KEY = "hook_system_ui_blurred_expanded_panel_radius_pref";
	public static String BLUR_RADIUS_PREFERENCE_DEFAULT = "2";

	public static String BLUR_COLOR_PREFERENCE_KEY = "hook_system_ui_blurred_expanded_panel_color_pref";
	public static int BLUR_COLOR_PREFERENCE_DEFAULT = Color.GRAY;

	public static String TRANSLUCENT_NOTIFICATIONS_PREFERENCE_KEY = "hook_system_ui_translucent_notifications_pref";
	public static boolean TRANSLUCENT_NOTIFICATIONS_PREFERENCE_DEFAULT = false;

	public static String PORTRAIT_MARGIN_PREFERENCE_KEY = "hook_system_ui_portrait_margin_pref";
	public static boolean PORTRAIT_MARGIN_PREFERENCE_DEFAULT = false;

	public static String LANDSCAPE_MARGIN_PREFERENCE_KEY = "hook_system_ui_landscape_margin_pref";
	public static boolean LANDSCAPE_MARGIN_PREFERENCE_DEFAULT = false;

	public static String BLURRED_FADE_IN_OUT_PREFERENCE_KEY = "hook_system_ui_blurred_fade_in_out_pref";

	private CharSequence[] mScaleEntries = { "10 (1:10)", "20 (1:20)",
			"30 (1:30)", "40 (1:40)", "50 (1:50)" };

	private static void updatePreferences() {

		XSharedPreferences prefs = Modul.getXposedXSharedPreferences();

		// atualiza
		mBlurredNotificationPanelEnabled = true;// prefs.getBoolean(BlurSettings_Fragment.BLUR_ENABLED_PREFERENCE_KEY,
												// BlurSettings_Fragment.BLUR_ENABLED_PREFERENCE_DEFAULT);

		// atualiza
		mBlurredNotificationPanelScale = Integer
				.parseInt(BLUR_SCALE_PREFERENCE_DEFAULT);
		mBlurredNotificationPanelRadius = Integer
				.parseInt(BLUR_RADIUS_PREFERENCE_DEFAULT);
		mBlurredNotificationPanelColor = BLUR_COLOR_PREFERENCE_DEFAULT;
		mAdjustmentsStartMarginPortrait = PORTRAIT_MARGIN_PREFERENCE_DEFAULT;
		mAdjustmentsStartMarginLandscape = LANDSCAPE_MARGIN_PREFERENCE_DEFAULT;

		// atualiza
		SystemUI_BaseStatusBar.updatePreferences(prefs);

		// atualiza
		SystemUI_PanelView.updatePreferences(prefs);

		// ImageView visível ?
		if (SystemUI_NotificationPanelView.mBlurredBackground != null) {
			SystemUI_NotificationPanelView.mBlurredBackground
					.setVisibility(mBlurredNotificationPanelEnabled ? View.VISIBLE
							: View.GONE);
		}

	}

	private static class BlurTask extends AsyncTask<Void, Void, Bitmap> {

		private int[] mScreenDimens;
		private Bitmap mScreenBitmap;

		@Override
		protected void onPreExecute() {

			long startMs = System.currentTimeMillis();

			// obtém o tamamho real da tela
			mScreenDimens = Utils.getRealScreenDimensions(mContext);

			// não comentar essa linha (utilizado pelo programa !!!)
			Log.d("xx_blur_time",
					"onPreExecute: " + (System.currentTimeMillis() - startMs)
							+ "ms");

		}

		@Override
		protected Bitmap doInBackground(Void... arg0) {

			long startMs = System.currentTimeMillis();

			// obtém a screenshot
			mScreenBitmap = Utils.takeSurfaceScreenshot(mContext);

			// continua ?
			if (mScreenBitmap == null)
				return null;

			// diminui o bitmap
			Bitmap scaled = Bitmap.createScaledBitmap(mScreenBitmap,
					mScreenDimens[0] / mBlurredNotificationPanelScale,
					mScreenDimens[1] / mBlurredNotificationPanelScale, true);

			// blur
			if (Utils.getAndroidAPILevel() >= 17) {

				// 4.2.2+
				scaled = Utils.Blur.renderScriptBlur(scaled,
						mBlurredNotificationPanelRadius);

			} else {

				// -4.1.2
				scaled = Utils.Blur.stackBlur(scaled,
						mBlurredNotificationPanelRadius);

			}

			// não comentar essa linha (utilizado pelo programa !!!)
			Log.d("xx_blur_time",
					"doInBackground: " + (System.currentTimeMillis() - startMs)
							+ "ms");

			return scaled;

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			long startMs = System.currentTimeMillis();

			if (bitmap != null) {

				// -----------------------------
				// bitmap criado com sucesso !!!
				// -----------------------------

				// seta o bitmap já com o efeito de desfoque
				SystemUI_NotificationPanelView.mBlurredBackground
						.setImageBitmap(bitmap);

				// seta a cor sobre o bitmap
				SystemUI_NotificationPanelView.mBlurredBackground
						.setColorFilter(mBlurredNotificationPanelColor,
								PorterDuff.Mode.MULTIPLY);

				// reseta o tag
				SystemUI_NotificationPanelView.mBlurredBackground.setTag("ok");

				// recicla o bitmap original
				mScreenBitmap.recycle();
				mScreenBitmap = null;

			} else {

				// --------------------------
				// erro ao criar o bitmap !!!
				// --------------------------

				// seta o filtro de cor
				SystemUI_NotificationPanelView.mBlurredBackground
						.setImageDrawable(new ColorDrawable(
								mBlurredNotificationPanelColor));

				// torna visível
				if (SystemUI_NotificationPanelView.mBlurredBackground
						.getAlpha() != 1.0f) {
					SystemUI_NotificationPanelView.mBlurredBackground
							.setAlpha(1.0f);
				}

				// seta o tag de erro
				SystemUI_NotificationPanelView.mBlurredBackground
						.setTag("error");

			}

			// não comentar essa linha (utilizado pelo programa !!!)
			Log.d("xx_blur_time",
					"onPostExecute: " + (System.currentTimeMillis() - startMs)
							+ "ms");

		}
	}
}