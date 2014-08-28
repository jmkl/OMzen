/*
 * Copyright (C) 2013 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello.dcsms.omzen.PowerMenu;

import hello.dcsms.omzen.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.Unhook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ModPowerMenu {
	private static final String TAG = "GB:ModPowerMenu";
	public static final String PACKAGE_NAME = "android";
	public static final String CLASS_GLOBAL_ACTIONS = "com.android.internal.policy.impl.GlobalActions";
	public static final String CLASS_ACTION = "com.android.internal.policy.impl.GlobalActions.Action";
	private static final boolean DEBUG = false;

	private static Context mContext;
	private static String mRebootStr;
	private static String mRebootSoftStr;
	private static String mRecoveryStr;
	private static String mBootloaderStr;
	private static Drawable mRebootIcon;
	private static Drawable mRebootSoftIcon;
	private static Drawable mRecoveryIcon;
	private static Drawable mBootloaderIcon;
	private static Drawable mExpandedDesktopIcon;
	private static Drawable mScreenshotIcon;
	private static List<IIconListAdapterItem> mRebootItemList;
	private static String mRebootConfirmStr;
	private static String mRebootConfirmRecoveryStr;
	private static String mRebootConfirmBootloaderStr;
	private static String mExpandedDesktopStr;
	private static String mExpandedDesktopOnStr;
	private static String mExpandedDesktopOffStr;
	private static String mScreenshotStr;
	private static Unhook mRebootActionHook;
	private static Object mRebootActionItem;
	private static boolean mRebootActionItemStockExists;
	private static Object mScreenshotAction;
	private static Object mExpandedDesktopAction;
	private static boolean mRebootConfirmRequired;
	private static boolean mRebootAllowOnLockscreen;

	private static void log(String message) {
		XposedBridge.log(TAG + ": " + message);
	}

	public static void init(final XSharedPreferences prefs,
			final ClassLoader classLoader) {

		try {
			String CLASS_PHONE_WINDOW_MANAGER = "com.android.internal.policy.impl.PhoneWindowManager";
			String CLASS_WINDOW_MANAGER_FUNCS = "android.view.WindowManagerPolicy.WindowManagerFuncs";
			String CLASS_IWINDOW_MANAGER = "android.view.IWindowManager";

			final Class<?> classPhoneWindowManager = XposedHelpers.findClass(
					CLASS_PHONE_WINDOW_MANAGER, null);

			XposedHelpers.findAndHookMethod(classPhoneWindowManager, "init",
					Context.class, CLASS_IWINDOW_MANAGER,
					CLASS_WINDOW_MANAGER_FUNCS, phoneWindowManagerInitHook);

			final Class<?> globalActionsClass = XposedHelpers.findClass(
					CLASS_GLOBAL_ACTIONS, classLoader);
			final Class<?> actionClass = XposedHelpers.findClass(CLASS_ACTION,
					classLoader);

			XposedBridge.hookAllConstructors(globalActionsClass,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(
								final MethodHookParam param) throws Throwable {
							mContext = (Context) param.args[0];
							Context gbContext = mContext.createPackageContext(
									"hello.dcsms.omzen",
									Context.CONTEXT_IGNORE_SECURITY);
							Resources res = mContext.getResources();
							Resources gbRes = gbContext.getResources();

							int rebootStrId = res.getIdentifier(
									"factorytest_reboot", "string",
									PACKAGE_NAME);

							mRebootStr = (rebootStrId == 0) ? "Reboot" : res
									.getString(rebootStrId);
							mRebootSoftStr = "Soft Reboot";
							mRecoveryStr = "Reboot Recovery";
							mBootloaderStr = "Reboot DroidBoot";
							mExpandedDesktopStr = "Expanded Desktop";
							mExpandedDesktopOnStr = "Expanded Desktop ON";
							mExpandedDesktopOffStr = "Expanded Desktop OFF";
							mScreenshotStr = "ScreenShot";

							mRebootIcon = gbRes
									.getDrawable(R.drawable.ic_lock_reboot);
							mRebootSoftIcon = gbRes
									.getDrawable(R.drawable.ic_lock_reboot_soft);
							mRecoveryIcon = gbRes
									.getDrawable(R.drawable.ic_lock_recovery);
							mBootloaderIcon = gbRes
									.getDrawable(R.drawable.ic_lock_bootloader);
							mExpandedDesktopIcon = gbRes.getDrawable(Utils
									.hasLenovoVibeUI() ? R.drawable.ic_lock_expanded_desktop_vibeui
									: R.drawable.ic_lock_expanded_desktop);
							mScreenshotIcon = gbRes.getDrawable(Utils
									.hasLenovoVibeUI() ? R.drawable.ic_lock_screenshot_vibeui
									: R.drawable.ic_lock_screenshot);

							// colorize icons to make them look good on light
							// backgrounds
							int airplaneIconId = res.getIdentifier(
									"ic_lock_airplane_mode_off", "drawable",
									PACKAGE_NAME);
							if (airplaneIconId != 0) {
								Drawable airplaneIcon = res
										.getDrawable(airplaneIconId);
								Bitmap bitmap = Utils
										.drawableToBitmap(airplaneIcon);
								int airplaneIconColor = Utils
										.getBitmapPredominantColor(bitmap);
								if (airplaneIconColor != Color.WHITE) {
									int iconColor = Utils.hasLenovoVibeUI() ? Color
											.parseColor("#4b4b4b") : Color.GRAY;
									mRebootIcon.setColorFilter(iconColor,
											PorterDuff.Mode.SRC_ATOP);
									mRebootSoftIcon.setColorFilter(iconColor,
											PorterDuff.Mode.SRC_ATOP);
									mRecoveryIcon.setColorFilter(iconColor,
											PorterDuff.Mode.SRC_ATOP);
									mBootloaderIcon.setColorFilter(iconColor,
											PorterDuff.Mode.SRC_ATOP);
									mExpandedDesktopIcon
											.setColorFilter(iconColor,
													PorterDuff.Mode.SRC_ATOP);
									mScreenshotIcon.setColorFilter(iconColor,
											PorterDuff.Mode.SRC_ATOP);
								}
							}

							mRebootItemList = new ArrayList<IIconListAdapterItem>();
							mRebootItemList.add(new BasicIconListItem(
									mRebootStr, null, mRebootIcon, null));
							mRebootItemList
									.add(new BasicIconListItem(mRebootSoftStr,
											null, mRebootSoftIcon, null));
							mRebootItemList.add(new BasicIconListItem(
									mRecoveryStr, null, mRecoveryIcon, null));
							if (!Utils.isMtkDevice()) {
								mRebootItemList.add(new BasicIconListItem(
										mBootloaderStr, null, mBootloaderIcon,
										null));
							}

							mRebootConfirmStr = "Reboot?";
							mRebootConfirmRecoveryStr = "Reboot Recovery?";
							mRebootConfirmBootloaderStr = "Reboot Droidboot?";

							if (DEBUG)
								log("GlobalActions constructed, resources set.");
						}
					});

			XposedHelpers.findAndHookMethod(globalActionsClass, "createDialog",
					new XC_MethodHook() {

						@Override
						protected void beforeHookedMethod(
								final MethodHookParam param) throws Throwable {
							if (mRebootActionHook != null) {
								if (DEBUG)
									log("Unhooking previous hook of reboot action item");
								mRebootActionHook.unhook();
								mRebootActionHook = null;
							}
						}

						@Override
						protected void afterHookedMethod(
								final MethodHookParam param) throws Throwable {
							if (mContext == null)
								return;

							mRebootConfirmRequired = true;
							mRebootAllowOnLockscreen = true;

							@SuppressWarnings("unchecked")
							List<Object> mItems = (List<Object>) XposedHelpers
									.getObjectField(param.thisObject, "mItems");
							BaseAdapter mAdapter = (BaseAdapter) XposedHelpers
									.getObjectField(param.thisObject,
											"mAdapter");
							int index = 1;

							// try to find out if reboot action item already
							// exists in the list of GlobalActions items
							// strategy:
							// 1) check if Action has mIconResId field or
							// mMessageResId field
							// 2) check if the name of the corresponding
							// resource contains "reboot" or "restart" substring
							if (mRebootActionItem == null) {
								if (DEBUG)
									log("Searching for existing reboot action item...");
								Resources res = mContext.getResources();
								for (Object o : mItems) {
									// search for drawable
									try {
										Field f = XposedHelpers.findField(
												o.getClass(), "mIconResId");
										String resName = res
												.getResourceEntryName(
														(Integer) f.get(o))
												.toLowerCase(Locale.US);
										if (DEBUG)
											log("Drawable resName = " + resName);
										if (resName.contains("reboot")
												|| resName.contains("restart")) {
											mRebootActionItem = o;
											break;
										}
									} catch (NoSuchFieldError nfe) {
										// continue
									} catch (Resources.NotFoundException resnfe) {
										// continue
									} catch (IllegalArgumentException iae) {
										// continue
									}

									if (mRebootActionItem == null) {
										// search for text
										try {
											Field f = XposedHelpers.findField(
													o.getClass(),
													"mMessageResId");
											String resName = res
													.getResourceEntryName(
															(Integer) f.get(o))
													.toLowerCase(Locale.US);
											if (DEBUG)
												log("Text resName = " + resName);
											if (resName.contains("reboot")
													|| resName
															.contains("restart")) {
												mRebootActionItem = o;
												break;
											}
										} catch (NoSuchFieldError nfe) {
											// continue
										} catch (Resources.NotFoundException resnfe) {
											// continue
										} catch (IllegalArgumentException iae) {
											// continue
										}
									}
								}

								if (mRebootActionItem == null) {
									if (DEBUG)
										log("Existing Reboot action item NOT found! Creating new RebootAction item");
									mRebootActionItemStockExists = false;
									mRebootActionItem = Proxy.newProxyInstance(
											classLoader,
											new Class<?>[] { actionClass },
											new RebootAction());
								} else {
									if (DEBUG)
										log("Existing Reboot action item found!");
									mRebootActionItemStockExists = true;
								}
							}

							// Add/hook reboot action if enabled
							if (prefs.getBoolean("MOD_POWER_MENU", false)) {
								if (mRebootActionItemStockExists) {
									mRebootActionHook = XposedHelpers
											.findAndHookMethod(
													mRebootActionItem
															.getClass(),
													"onPress",
													new XC_MethodReplacement() {
														@Override
														protected Object replaceHookedMethod(
																MethodHookParam param)
																throws Throwable {
															RebootAction
																	.showRebootDialog(mContext);
															return null;
														}
													});
								} else {
									// add to the second position
									mItems.add(index, mRebootActionItem);
								}
								index++;
							} else if (mRebootActionItemStockExists) {
								index++;
							}

							// Add screenshot action if enabled
							if (true) {
								if (mScreenshotAction == null) {
									mScreenshotAction = Proxy.newProxyInstance(
											classLoader,
											new Class<?>[] { actionClass },
											new ScreenshotAction());
									if (DEBUG)
										log("mScreenshotAction created");
								}
								mItems.add(index++, mScreenshotAction);
							}

							mAdapter.notifyDataSetChanged();
						}
					});

			XposedHelpers.findAndHookMethod(globalActionsClass, "showDialog",
					boolean.class, boolean.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(
								final MethodHookParam param) throws Throwable {
							prefs.reload();
							if (true) {
								boolean locked = (Boolean) param.args[0];
								if (!locked) {
									// double-check using keyguard manager
									try {
										Context context = (Context) XposedHelpers
												.getObjectField(
														param.thisObject,
														"mContext");
										KeyguardManager km = (KeyguardManager) context
												.getSystemService(Context.KEYGUARD_SERVICE);
										locked = km.isKeyguardLocked();
									} catch (Throwable t) {
									}
								}

								if (locked) {
									Dialog d = (Dialog) XposedHelpers
											.getObjectField(param.thisObject,
													"mDialog");
									if (d == null) {
										XposedHelpers.callMethod(
												param.thisObject,
												"createDialog");
									}
									param.setResult(null);
								}
							}
						}
					});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	private static class RebootAction implements InvocationHandler {
		private Context mContext;

		public RebootAction() {
		}

		public static void showRebootDialog(final Context context) {
			if (context == null) {
				if (DEBUG)
					log("Context is null - aborting");
				return;
			}

			try {
				if (DEBUG)
					log("about to build reboot dialog");

				AlertDialog.Builder builder = new AlertDialog.Builder(context)
						.setTitle(mRebootStr)
						.setAdapter(
								new IconListAdapter(context, mRebootItemList),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										if (DEBUG)
											log("onClick() item = " + which);
										handleReboot(context, mRebootStr, which);
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
				AlertDialog dialog = builder.create();
				dialog.getWindow().setType(
						WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
				dialog.show();
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

		private static void doReboot(Context context, int mode) {
			final PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			if (mode == 0) {
				pm.reboot(null);
			} else if (mode == 1) {
				Utils.performSoftReboot();
			} else if (mode == 2) {
				pm.reboot("recovery");
			} else if (mode == 3) {
				pm.reboot("bootloader");
			}
		}

		private static void handleReboot(final Context context, String caption,
				final int mode) {
			try {
				if (!mRebootConfirmRequired) {
					doReboot(context, mode);
				} else {
					String message = mRebootConfirmStr;
					if (mode == 2) {
						message = mRebootConfirmRecoveryStr;
					} else if (mode == 3) {
						message = mRebootConfirmBootloaderStr;
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context)
							.setTitle(caption)
							.setMessage(message)
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											doReboot(context, mode);
										}
									})
							.setNegativeButton(android.R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
					AlertDialog dialog = builder.create();
					dialog.getWindow().setType(
							WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
					dialog.show();
				}
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodName = method.getName();

			if (methodName.equals("create")) {
				mContext = (Context) args[0];
				Resources res = mContext.getResources();
				LayoutInflater li = (LayoutInflater) args[3];
				int layoutId = res.getIdentifier("global_actions_item",
						"layout", "android");
				View v = li.inflate(layoutId, (ViewGroup) args[2], false);

				ImageView icon = (ImageView) v.findViewById(res.getIdentifier(
						"icon", "id", "android"));
				icon.setImageDrawable(mRebootIcon);

				TextView messageView = (TextView) v.findViewById(res
						.getIdentifier("message", "id", "android"));
				messageView.setText(mRebootStr);

				TextView statusView = (TextView) v.findViewById(res
						.getIdentifier("status", "id", "android"));
				statusView.setVisibility(View.GONE);

				return v;
			} else if (methodName.equals("onPress")) {
				showRebootDialog(mContext);
				return null;
			} else if (methodName.equals("onLongPress")) {
				handleReboot(mContext, mRebootStr, 0);
				return true;
			} else if (methodName.equals("showDuringKeyguard")) {
				return mRebootAllowOnLockscreen;
			} else if (methodName.equals("showBeforeProvisioning")) {
				return true;
			} else if (methodName.equals("isEnabled")) {
				return true;
			} else if (methodName.equals("showConditional")) {
				return true;
			} else {
				log("RebootAction: Unhandled invocation method: " + methodName);
				return null;
			}
		}
	}

	private static class ExpandedDesktopAction implements InvocationHandler {
		private Context mContext;
		private TextView mStatus;
		private Handler mHandler;

		public ExpandedDesktopAction() {
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodName = method.getName();

			if (methodName.equals("create")) {
				mContext = (Context) args[0];
				mHandler = new Handler();

				Resources res = mContext.getResources();
				LayoutInflater li = (LayoutInflater) args[3];
				int layoutId = res.getIdentifier("global_actions_item",
						"layout", "android");
				View v = li.inflate(layoutId, (ViewGroup) args[2], false);

				ImageView icon = (ImageView) v.findViewById(res.getIdentifier(
						"icon", "id", "android"));
				icon.setImageDrawable(mExpandedDesktopIcon);

				TextView messageView = (TextView) v.findViewById(res
						.getIdentifier("message", "id", "android"));
				messageView.setText(mExpandedDesktopStr);

				mStatus = (TextView) v.findViewById(res.getIdentifier("status",
						"id", "android"));
				mStatus.setVisibility(View.VISIBLE);

				return v;
			} else if (methodName.equals("onPress")) {

				return null;
			} else if (methodName.equals("onLongPress")) {
				return false;
			} else if (methodName.equals("showDuringKeyguard")) {
				return true;
			} else if (methodName.equals("showBeforeProvisioning")) {
				return true;
			} else if (methodName.equals("isEnabled")) {
				return true;
			} else if (methodName.equals("showConditional")) {
				return true;
			} else {
				log("ExpandedDesktopAction: Unhandled invocation method: "
						+ methodName);
				return null;
			}
		}
	}

	private static XC_MethodHook phoneWindowManagerInitHook = new XC_MethodHook() {
		@Override
		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			mPhoneWindowManager = param.thisObject;
		}
	};
	private static Object mPhoneWindowManager;

	private static class ScreenshotAction implements InvocationHandler {
		private static Context mContext;
		private static final Object mScreenshotLock = new Object();
		private static ServiceConnection mScreenshotConnection = null;
	    private static final Runnable mScreenshotTimeout = new Runnable() {
	        @Override
	        public void run() {
	            synchronized (mScreenshotLock) {
	                if (mScreenshotConnection != null) {
	                    mContext.unbindService(mScreenshotConnection);
	                    mScreenshotConnection = null;
	                }
	            }
	        }
	    };

		private static void takeScreenshot() {
			final Handler handler = (Handler) XposedHelpers.getObjectField(
					mPhoneWindowManager, "mHandler");
			if (handler == null)
				return;

			synchronized (mScreenshotLock) {
				if (mScreenshotConnection != null) {
					return;
				}
				ComponentName cn = new ComponentName("com.android.systemui",
						"com.android.systemui.screenshot.TakeScreenshotService");
				Intent intent = new Intent();
				intent.setComponent(cn);
				ServiceConnection conn = new ServiceConnection() {
					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized (mScreenshotLock) {
							if (mScreenshotConnection != this) {
								return;
							}
							final Messenger messenger = new Messenger(service);
							final Message msg = Message.obtain(null, 1);
							final ServiceConnection myConn = this;

							Handler h = new Handler(handler.getLooper()) {
								@Override
								public void handleMessage(Message msg) {
									synchronized (mScreenshotLock) {
										if (mScreenshotConnection == myConn) {
											mContext.unbindService(mScreenshotConnection);
											mScreenshotConnection = null;
											handler.removeCallbacks(mScreenshotTimeout);
										}
									}
								}
							};
							msg.replyTo = new Messenger(h);
							msg.arg1 = msg.arg2 = 0;
							h.postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										messenger.send(msg);
									} catch (RemoteException e) {
										XposedBridge.log(e);
									}
								}
							}, 1000);
						}
					}

					@Override
					public void onServiceDisconnected(ComponentName name) {
					}
				};
				if (mContext
						.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
					mScreenshotConnection = conn;
					handler.postDelayed(mScreenshotTimeout, 10000);
				}
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodName = method.getName();

			if (methodName.equals("create")) {
				mContext = (Context) args[0];
				Resources res = mContext.getResources();
				LayoutInflater li = (LayoutInflater) args[3];
				int layoutId = res.getIdentifier("global_actions_item",
						"layout", "android");
				View v = li.inflate(layoutId, (ViewGroup) args[2], false);

				ImageView icon = (ImageView) v.findViewById(res.getIdentifier(
						"icon", "id", "android"));
				icon.setImageDrawable(mScreenshotIcon);

				TextView messageView = (TextView) v.findViewById(res
						.getIdentifier("message", "id", "android"));
				messageView.setText(mScreenshotStr);

				TextView statusView = (TextView) v.findViewById(res
						.getIdentifier("status", "id", "android"));
				statusView.setVisibility(View.GONE);

				return v;
			} else if (methodName.equals("onPress")) {
				takeScreenshot();
				return null;
			} else if (methodName.equals("onLongPress")) {
				return true;
			} else if (methodName.equals("showDuringKeyguard")) {
				return true;
			} else if (methodName.equals("showBeforeProvisioning")) {
				return true;
			} else if (methodName.equals("isEnabled")) {
				return true;
			} else if (methodName.equals("showConditional")) {
				return true;
			} else {
				log("ScreenshotAction: Unhandled invocation method: "
						+ methodName);
				return null;
			}
		}
	}
}