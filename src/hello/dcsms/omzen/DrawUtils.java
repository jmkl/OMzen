package hello.dcsms.omzen;

import java.io.File;

import android.content.res.ColorStateList;
import android.content.res.XResources;
import android.content.res.XResources.DrawableLoader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class DrawUtils {
	public static void setTypeFace(View v) {
		XSharedPreferences pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();
		boolean usecustomfont = pref.getBoolean("CUSTOM_FONT", false);
		if (usecustomfont) {
			Typeface tf = Typeface.createFromFile(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/default_icon" + "/font_satu.ttf");
			((TextView) v).setTypeface(tf);
		}
	}

	public static Drawable getDraw9(
			
			final InitPackageResourcesParam resparam, final String nama) {
		Bitmap bitmap = BitmapFactory.decodeFile(S.DEFAULT_ICON_DIR + "/"
				+ nama + ".png");
		final byte[] chunk = bitmap.getNinePatchChunk();
		if (NinePatch.isNinePatchChunk(chunk))
			return new NinePatchDrawable(resparam.res, bitmap, chunk,
					Npatch.deserialize(chunk).mPaddings, null);
		else
			return Drawable.createFromPath(S.DEFAULT_ICON_DIR + "/" + nama
					+ ".png");

	}
	public static DrawableLoader getDrawableNine(
			final InitPackageResourcesParam resparam, final String nama) {
		DrawableLoader dx = new XResources.DrawableLoader() {

			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				Bitmap bitmap = BitmapFactory.decodeFile(S.DEFAULT_ICON_DIR
						+ "/" + nama + ".png");
				final byte[] chunk = bitmap.getNinePatchChunk();
				if (NinePatch.isNinePatchChunk(chunk))
					return new NinePatchDrawable(resparam.res, bitmap, chunk,
							Npatch.deserialize(chunk).mPaddings, null);
				else
					return Drawable.createFromPath(S.DEFAULT_ICON_DIR + "/"
							+ nama + ".png");
			}
		};
		return dx;

	}

	public static DrawableLoader getColorDrawable(final int kolor) {

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				Drawable d = new ColorDrawable(kolor);
				return d;
			}
		};
		return dx;
	}

	public static DrawableLoader stateDrawable() {
		DrawableLoader dx = new XResources.DrawableLoader() {

			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				final File n = new File(S.DEFAULT_ICON_DIR
						+ "/toggle_btn_bg_n.png");
				final File on = new File(S.DEFAULT_ICON_DIR
						+ "/toggle_btn_bg_on.png");
				final File off = new File(S.DEFAULT_ICON_DIR
						+ "/toggle_btn_bg_off.png");
				XSharedPreferences pref = new XSharedPreferences(
						"hello.dcsms.omzen");
				pref.makeWorldReadable();
				BitmapDrawable a = (BitmapDrawable) Drawable.createFromPath(n
						.getAbsolutePath());
				BitmapDrawable b = (BitmapDrawable) Drawable.createFromPath(on
						.getAbsolutePath());
				BitmapDrawable c = (BitmapDrawable) Drawable.createFromPath(off
						.getAbsolutePath());

				a.setTargetDensity(pref.getInt("DENSITY", 240));

				b.setTargetDensity(pref.getInt("DENSITY", 240));

				c.setTargetDensity(pref.getInt("DENSITY", 240));
				a.setGravity(Gravity.CENTER);
				b.setGravity(Gravity.CENTER);
				c.setGravity(Gravity.CENTER);

				StateListDrawable sld = new StateListDrawable();
				sld.addState(new int[] { android.R.attr.state_pressed,
						android.R.attr.state_enabled }, a);
				sld.addState(new int[] { android.R.attr.state_checked,
						android.R.attr.state_enabled }, b);
				sld.addState(new int[] {}, c);
				return sld;
			}
		};
		return dx;

	}

	public static String getIconPath(String nama) {
		return S.DEFAULT_ICON_DIR + "/" + nama + ".png";
	}

	public static DrawableLoader stateDrawableIcon(final int white,
			final int transparent, final int black, final Drawable d) {
		final File n = new File(S.DEFAULT_ICON_DIR + "/toggle_btn_bg_n.png");
		final File on = new File(S.DEFAULT_ICON_DIR + "/toggle_btn_bg_on.png");
		final File off = new File(S.DEFAULT_ICON_DIR + "/toggle_btn_bg_off.png");
		DrawableLoader dx = new XResources.DrawableLoader() {

			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				StateListDrawable sld = new StateListDrawable();

				sld.addState(new int[] { android.R.attr.state_pressed,
						android.R.attr.state_enabled },
						Drawable.createFromPath(n.getAbsolutePath()));
				sld.addState(new int[] { android.R.attr.state_checked,
						android.R.attr.state_enabled },
						Drawable.createFromPath(n.getAbsolutePath()));
				sld.addState(new int[] {},
						Drawable.createFromPath(n.getAbsolutePath()));
				sld.addState(new int[] {}, d);

				return sld;
			}
		};
		return dx;

	}

	public static DrawableLoader getLayerDrawable(final String nama) {

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				XSharedPreferences pref = new XSharedPreferences(
						"hello.dcsms.omzen");
				pref.makeWorldReadable();
				File f = new File(S.DEFAULT_ICON_DIR + "/" + nama + ".png");
				BitmapDrawable d = (BitmapDrawable) Drawable
						.createFromPath(S.DEFAULT_ICON_DIR
								+ "/toggle_btn_bg_off.png");
				BitmapDrawable d2 = (BitmapDrawable) Drawable.createFromPath(f
						.getAbsolutePath());
				d.setGravity(Gravity.CENTER);
				d.setTargetDensity(pref.getInt("DENSITY", 240));
				d2.setTargetDensity(pref.getInt("DENSITY", 240));
				d2.setGravity(Gravity.CENTER);
				Drawable[] dd = new Drawable[] { d, d2 };
				LayerDrawable dx = new LayerDrawable(dd);

				return dx;
			}
		};
		return dx;
	}

	public static DrawableLoader getIcon(String nama) {
		final File f = new File(S.DEFAULT_ICON_DIR + "/" + nama + ".png");

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				XSharedPreferences pref = new XSharedPreferences(
						"hello.dcsms.omzen");
				pref.makeWorldReadable();
				BitmapDrawable d = (BitmapDrawable) Drawable.createFromPath(f
						.getAbsolutePath());
				d.setTargetDensity(pref.getInt("DENSITY", 240));
				return d;
			}
		};
		return dx;
	}

	public static DrawableLoader stateDrawableIcon(String nama) {
		final File n = new File(S.DEFAULT_ICON_DIR + "/toggle_btn_bg_n.png");

		final File f = new File(S.DEFAULT_ICON_DIR + "/" + nama + ".png");

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				XSharedPreferences pref = new XSharedPreferences(
						"hello.dcsms.omzen");
				pref.makeWorldReadable();
				BitmapDrawable d = (BitmapDrawable) Drawable.createFromPath(n
						.getAbsolutePath());

				BitmapDrawable d2 = (BitmapDrawable) Drawable.createFromPath(f
						.getAbsolutePath());
				d.setTargetDensity(pref.getInt("DENSITY", 240));
				d2.setTargetDensity(pref.getInt("DENSITY", 240));
				d.setGravity(Gravity.CENTER);
				d2.setGravity(Gravity.CENTER);
				Drawable[] dd = new Drawable[] { d, d2 };
				LayerDrawable dx = new LayerDrawable(dd);
				return dx;
			}
		};
		return dx;
	}

	private static Drawable getNinePathDrawable(
			InitPackageResourcesParam resparam, String str) {
		Bitmap bitmap = BitmapFactory.decodeFile(str);
		final byte[] chunk = bitmap.getNinePatchChunk();
		Drawable d;
		if (NinePatch.isNinePatchChunk(chunk)) {
			d = new NinePatchDrawable(resparam.res, bitmap, chunk,
					Npatch.deserialize(chunk).mPaddings, null);
		} else {
			d = Drawable.createFromPath(str);
		}

		return d;

	}

	public static DrawableLoader getSeekbarDrawable(
			final InitPackageResourcesParam resparam) {
		final File seek2 = new File(S.DEFAULT_ICON_DIR
				+ "/asus_brightness_control_gray_bg.9.png");
		final File seek1 = new File(S.DEFAULT_ICON_DIR
				+ "/asus_brightness_control_hl_bg.9.png");
		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				Bitmap bitmap = BitmapFactory.decodeFile(seek1
						.getAbsolutePath());
				final byte[] chunk = bitmap.getNinePatchChunk();
				Drawable d;
				if (NinePatch.isNinePatchChunk(chunk)) {
					d = new NinePatchDrawable(resparam.res, bitmap, chunk,
							Npatch.deserialize(chunk).mPaddings, null);
				} else {
					d = Drawable.createFromPath(seek1.getAbsolutePath());
				}

				ClipDrawable clip = new ClipDrawable(d, Gravity.LEFT,
						ClipDrawable.HORIZONTAL);
				Bitmap bitmap2 = BitmapFactory.decodeFile(seek2
						.getAbsolutePath());
				Drawable d2;
				final byte[] chunk2 = bitmap2.getNinePatchChunk();
				if (NinePatch.isNinePatchChunk(chunk2)) {
					d2 = new NinePatchDrawable(resparam.res, bitmap2, chunk2,
							Npatch.deserialize(chunk2).mPaddings, null);
				} else {
					d2 = Drawable.createFromPath(seek2.getAbsolutePath());
				}

				InsetDrawable isd = new InsetDrawable(d2, 0);
				LayerDrawable ld = new LayerDrawable(
						new Drawable[] { isd, clip });
				return ld;

			};
		};
		return dx;
	}

	public static DrawableLoader getToggleDrawableState(final String string) {
		String on = null;
		String off = null;
		if (string.equals("asus_phone_control_autorotate_bg")) {
			on = "asus_ep_statusicon_auto_rotate_on";
			off = "asus_ep_statusicon_auto_rotate_off";
		} else if (string.equals("asus_phone_control_autosync_bg")) {
			on = "asus_ep_statusicon_autosync_on";
			off = "asus_ep_statusicon_autosync_off";
		} else if (string.equals("asus_phone_control_bluetooth_bg")) {
			on = "asus_ep_statusicon_bluetooth_on";
			off = "asus_ep_statusicon_bluetooth_off";
		} else if (string.equals("asus_phone_control_flightmode_bg")) {
			on = "asus_ep_statusicon_flightmode_on";
			off = "asus_ep_statusicon_flightmode_off";
		} else if (string.equals("asus_phone_control_gps_bg")) {
			on = "asus_ep_statusicon_gps_on";
			off = "asus_ep_statusicon_gps_off";
		} else if (string.equals("asus_phone_control_miracast_bg")) {
			on = "asus_ep_statusicon_miracast_setting_on";
			off = "asus_ep_statusicon_miracast_setting_off";
		} else if (string.equals("asus_phone_control_mobiledata_bg")) {
			on = "asus_ep_statusicon_data_on";
			off = "asus_ep_statusicon_data_off";
		} else if (string.equals("asus_phone_control_powersaving2_bg")) {
			on = "asus_ep_statusicon_pavingnew_on";
			off = "asus_ep_statusicon_pavingnew_off";
		} else if (string.equals("asus_phone_control_silent_bg")) {
			on = "asus_ep_statusicon_silent";
			off = "asus_ep_statusicon_silent_off";
		} else if (string.equals("asus_phone_control_wifisharing_bg")) {
			on = "asus_ep_statusicon_wifisharing_on";
			off = "asus_ep_statusicon_wifisharing_off";
		} else if (string.equals("asus_phone_control_wifi_bg")) {
			on = "asus_ep_statusicon_wifi_on";
			off = "asus_ep_statusicon_wifi_off";
		} else if (string.equals("vibrate")) {
			on = "asus_ep_statusicon_vibrate";
			off = "asus_ep_statusicon_vibrate";
		} else if (string.equals("mute")) {
			on = "asus_ep_statusicon_silent";
			off = "asus_ep_statusicon_silent";
		} else if (string.equals("sound")) {
			on = "asus_ep_statusicon_sound";
			off = "asus_ep_statusicon_sound";
		} else if (string.equals("silent")) {
			on = "asus_ep_statusicon_silent";
			off = "asus_ep_statusicon_silent";
		} else if (string.equals("asus_phone_quickbox_audio")) {
			on = "asus_ep_statusicon_audio_wizard_on";
			off = "asus_ep_statusicon_audio_wizard_on";
		} else if (string.equals("asus_phone_quickbox_calculator")) {
			on = "asus_ep_statusicon_calculator_on";
			off = "asus_ep_statusicon_calculator_on";
		} else if (string.equals("asus_phone_quickbox_clean_memory_green")) {
			on = "asus_ep_statusicon_clean";
			off = "asus_ep_statusicon_clean";
		} else if (string.equals("asus_phone_quickbox_clean_memory_red")) {
			on = "asus_ep_statusicon_clean";
			off = "asus_ep_statusicon_clean";
		} else if (string.equals("asus_phone_quickbox_clean_memory_yellow")) {
			on = "asus_ep_statusicon_clean";
			off = "asus_ep_statusicon_clean";
		} else if (string.equals("asus_phone_quickbox_soundrecord")) {
			on = "asus_ep_statusicon_voice_recorder_on";
			off = "asus_ep_statusicon_voice_recorder_on";
		}
		final File ic_on = new File(S.DEFAULT_ICON_DIR + "/" + on + ".png");
		final File ic_off = new File(S.DEFAULT_ICON_DIR + "/" + off + ".png");
		final XSharedPreferences pref = new XSharedPreferences(
				"hello.dcsms.omzen");

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				StateListDrawable sld = new StateListDrawable();
				BitmapDrawable bd_on = (BitmapDrawable) Drawable
						.createFromPath(ic_on.getAbsolutePath());
				BitmapDrawable bd_off = (BitmapDrawable) Drawable
						.createFromPath(ic_off.getAbsolutePath());
				int dens = pref.getInt("DENSITY", 240);

				bd_on.setTargetDensity((int) (dens / 1.5));
				bd_off.setTargetDensity((int) (dens / 1.5));

				bd_on.setGravity(Gravity.CENTER);
				bd_off.setGravity(Gravity.CENTER);
				sld.addState(new int[] { android.R.attr.state_checked,
						android.R.attr.state_enabled }, bd_on);
				sld.addState(new int[] { -android.R.attr.state_checked,
						android.R.attr.state_enabled }, bd_on);
				sld.addState(new int[] {}, bd_off);
				// bg

				String _n = "toggle_btn_bg_n";
				String _on = "toggle_btn_bg_on";
				String _off = "toggle_btn_bg_off";
				if (string.equals("asus_phone_quickbox_clean_memory_yellow")) {
					_n = "toggle_btn_bg_n_y";
					_on = "toggle_btn_bg_on_y";
					_off = "toggle_btn_bg_off_y";
				}
				File n = new File(S.DEFAULT_ICON_DIR + "/" + _n + ".png");
				File on = new File(S.DEFAULT_ICON_DIR + "/" + _on + ".png");
				File off = new File(S.DEFAULT_ICON_DIR + "/" + _off + ".png");
				BitmapDrawable a = (BitmapDrawable) Drawable.createFromPath(n
						.getAbsolutePath());
				BitmapDrawable b = (BitmapDrawable) Drawable.createFromPath(on
						.getAbsolutePath());
				BitmapDrawable c = (BitmapDrawable) Drawable.createFromPath(off
						.getAbsolutePath());
				a.setGravity(Gravity.CENTER);
				b.setGravity(Gravity.CENTER);
				c.setGravity(Gravity.CENTER);

				b.setTargetDensity((int) (dens / 1.5));
				a.setTargetDensity((int) (dens / 1.5));
				c.setTargetDensity((int) (dens / 1.5));

				StateListDrawable sldb = new StateListDrawable();
				sldb.addState(new int[] { android.R.attr.state_pressed,
						android.R.attr.state_enabled }, a);
				sldb.addState(new int[] { android.R.attr.state_checked,
						android.R.attr.state_enabled }, b);
				sldb.addState(new int[] {}, c);

				LayerDrawable ld = new LayerDrawable(
						new Drawable[] { sldb, sld });
				return ld;
			}
		};
		return dx;
	}

	public static DrawableLoader getClearButton(
			final InitPackageResourcesParam resparam) {
		final File norm = new File(S.DEFAULT_ICON_DIR
				+ "/clear_button_normal.9.png");
		final File press = new File(S.DEFAULT_ICON_DIR
				+ "/clear_button_disable.9.png");
		final File disable = new File(S.DEFAULT_ICON_DIR
				+ "/clear_button_press.9.png");

		DrawableLoader dx = new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				XSharedPreferences pref = new XSharedPreferences(
						"hello.dcsms.omzen");
				pref.makeWorldReadable();
				NinePatchDrawable n = (NinePatchDrawable) getNinePathDrawable(
						resparam, norm.getAbsolutePath());
				NinePatchDrawable p = (NinePatchDrawable) getNinePathDrawable(
						resparam, press.getAbsolutePath());
				NinePatchDrawable d = (NinePatchDrawable) getNinePathDrawable(
						resparam, disable.getAbsolutePath());

				n.setTargetDensity(pref.getInt("DENSITY", 240));
				p.setTargetDensity(pref.getInt("DENSITY", 240));
				d.setTargetDensity(pref.getInt("DENSITY", 240));

				StateListDrawable dx = new StateListDrawable();
				StateListDrawable sldb = new StateListDrawable();
				sldb.addState(new int[] { android.R.attr.state_pressed, }, p);
				sldb.addState(new int[] { -android.R.attr.state_enabled, }, d);
				sldb.addState(new int[] {}, n);
				return sldb;
			}
		};
		return dx;
	}

	public static DrawableLoader Trans() {
		DrawableLoader d = new DrawableLoader() {

			@Override
			public Drawable newDrawable(XResources res, int id)
					throws Throwable {
				return new ColorDrawable(Color.TRANSPARENT);
			}
		};
		return d;
	}

	public static ColorStateList getColorStateList() {
		XSharedPreferences pref = new XSharedPreferences("hello.dcsms.omzen");
		pref.makeWorldReadable();
		int[][] states = new int[][] {
				new int[] { android.R.attr.state_pressed }, new int[] {} };
		int[] colors = new int[] { Color.GRAY,
				pref.getInt("WARNA_TEKS_TOGEL", Color.WHITE) };
		ColorStateList c = new ColorStateList(states, colors);
		return c;
	}

}
