package hello.dcsms.omzen.serajrblur;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import de.robv.android.xposed.XposedHelpers;

public class Utils {

	public static int getAndroidAPILevel() {

		// retorna o número da API en que o programa está rodando
		return android.os.Build.VERSION.SDK_INT;

	}

	public static ArrayList<View> getAllChildrenViews(View view) {

		if (!(view instanceof ViewGroup)) {

			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(view);

			return viewArrayList;

		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup viewGroup = (ViewGroup) view;
		for (int i = 0; i < viewGroup.getChildCount(); i++) {

			View child = viewGroup.getChildAt(i);

			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(view);
			viewArrayList.addAll(getAllChildrenViews(child));

			result.addAll(viewArrayList);
		}

		return result;

	}

	public static int[] getRealScreenDimensions(Context context) {

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getRealMetrics(metrics);

		return new int[] { metrics.widthPixels, metrics.heightPixels };

	}

	public static Bitmap takeSurfaceScreenshot(Context context) {

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		Matrix displayMatrix = new Matrix();

		Bitmap screenBitmap = null;

		display.getRealMetrics(metrics);
		float[] dims = { metrics.widthPixels, metrics.heightPixels };
		float degrees = getDegreesForRotation(display.getRotation());
		boolean requiresRotation = degrees > 0;

		if (requiresRotation) {

			// Get the dimensions of the device in its native orientation
			displayMatrix.reset();
			displayMatrix.preRotate(-degrees);
			displayMatrix.mapPoints(dims);
			dims[0] = Math.abs(dims[0]);
			dims[1] = Math.abs(dims[1]);

		}

		if (getAndroidAPILevel() >= 18) {

			// 4.3+
			screenBitmap = SurfaceControl.screenshot((int) dims[0],
					(int) dims[1]);

		} else {

			// 4.1.2 e 4.2.2
			try {

				// reflection
				Class<?> Surface = Class.forName("android.view.Surface");
				screenBitmap = (Bitmap) XposedHelpers.callStaticMethod(Surface,
						"screenshot", (int) dims[0], (int) dims[1]);

			} catch (ClassNotFoundException e) {

				e.printStackTrace();

			}
		}

		// possível app que precisa de segurança rodando, ou
		// o context não tem previlégios suficientes par tal
		if (screenBitmap == null) {

			// informa e retorna
			Log.i("serajr_blurred_system_ui",
					"Cannot take surface screenshot! Skipping blur feature!!");
			return null;

		}

		if (requiresRotation) {

			// Rotate the screenshot to the current orientation
			Bitmap ss = Bitmap.createBitmap(metrics.widthPixels,
					metrics.heightPixels, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(ss);
			c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
			c.rotate(360f - degrees);
			c.translate(-dims[0] / 2, -dims[1] / 2);
			c.drawBitmap(screenBitmap, 0, 0, null);
			c.setBitmap(null);
			screenBitmap = ss;

		}

		// Optimizations
		screenBitmap.setHasAlpha(false);
		screenBitmap.prepareToDraw();

		// retorna
		return screenBitmap;

	}

	private static float getDegreesForRotation(int value) {

		switch (value) {

		case Surface.ROTATION_90:
			return 90f;

		case Surface.ROTATION_180:
			return 180f;

		case Surface.ROTATION_270:
			return 270f;

		}

		return 0f;

	}

	@SuppressLint("NewApi")
	public static class Blur {

		private static RenderScript mRenderScript;
		private static ScriptIntrinsicBlur mScriptIntrinsicBlur;

		public static void initRenderScript(Context context) {

			// inicia os campos estáticos
			if (mRenderScript == null) {

				mRenderScript = RenderScript.create(context);
				mScriptIntrinsicBlur = ScriptIntrinsicBlur.create(
						mRenderScript, Element.U8_4(mRenderScript));

			}
		}

		public static Bitmap renderScriptBlur(Bitmap bitmap, int radius) {

			// não iniciado ?
			if (mRenderScript == null)
				return null;

			Allocation input = Allocation.createFromBitmap(mRenderScript,
					bitmap);
			Allocation output = Allocation.createTyped(mRenderScript,
					input.getType());
			mScriptIntrinsicBlur.setRadius(radius);
			mScriptIntrinsicBlur.setInput(input);
			mScriptIntrinsicBlur.forEach(output);
			output.copyTo(bitmap);

			return bitmap;

		}

		public static Bitmap stackBlur(Bitmap bitmap, int radius) {

			if (radius < 1)
				return null;

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);

			int wm = w - 1;
			int hm = h - 1;
			int wh = w * h;
			int div = radius + radius + 1;

			int r[] = new int[wh];
			int g[] = new int[wh];
			int b[] = new int[wh];
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
			int vmin[] = new int[Math.max(w, h)];

			int divsum = div + 1 >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];

			for (i = 0; i < 256 * divsum; i++) {

				dv[i] = i / divsum;

			}

			yw = yi = 0;

			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;

			for (y = 0; y < h; y++) {

				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				for (i = -radius; i <= radius; i++) {

					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = p & 0x0000ff;
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;

					if (i > 0) {

						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];

					} else {

						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];

					}
				}

				stackpointer = radius;

				for (x = 0; x < w; x++) {

					r[yi] = dv[rsum];
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (y == 0) {

						vmin[x] = Math.min(x + radius + 1, wm);

					}

					p = pix[yw + vmin[x]];

					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = p & 0x0000ff;

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer % div];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi++;

				}

				yw += w;

			}

			for (x = 0; x < w; x++) {

				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;

				for (i = -radius; i <= radius; i++) {

					yi = Math.max(0, yp) + x;

					sir = stack[i + radius];

					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];

					rbs = r1 - Math.abs(i);

					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;

					if (i > 0) {

						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];

					} else {

						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];

					}

					if (i < hm) {

						yp += w;

					}
				}

				yi = x;
				stackpointer = radius;

				for (y = 0; y < h; y++) {

					pix[yi] = 0xff000000 & pix[yi] | dv[rsum] << 16
							| dv[gsum] << 8 | dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (x == 0) {

						vmin[y] = Math.min(y + r1, hm) * w;

					}

					p = x + vmin[y];

					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi += w;

				}
			}

			bitmap.setPixels(pix, 0, w, 0, 0, w, h);

			return bitmap;

		}
	}
}