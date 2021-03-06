package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.XModuleResources;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LayoutInflated.LayoutInflatedParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XperiaNavBarButtons implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage {
	@SuppressWarnings("unused")
	private static final String TAG = "XperiaNavBarButtons";
	public static final String PACKAGE_NAME = XperiaNavBarButtons.class.getPackage().getName();
	public static final String CLASSNAME_SYSTEMUI = "com.android.systemui";
	public static final String CLASSNAME_NAVIGATIONBARVIEW = "com.android.systemui.statusbar.phone.NavigationBarView";
	private static String MODULE_PATH = null;
	private final static String DEF_BUTTONS_ORDER_LIST = "Search,Recent,Back,Home,Menu,Power";

	final static int BUTTONACTION_KILLAPP_ACTION = 996;
	final static int BUTTONACTION_CUSTOM_ACTION = 997;
	final static int BUTTONACTION_STATUSBAR_ACTION = 998;
	final static int BUTTONACTION_DO_NOTHING = 999;

	Context mContext;
	public static XModuleResources modRes;
	public static XSharedPreferences pref;
	int mDisabledFlags;
	Boolean mNavigationBarCanMove;
	public static Boolean STATUSBAR_EXPAND = false;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;
		modRes = XModuleResources.createInstance(MODULE_PATH, null);

		pref = new XSharedPreferences(XperiaNavBarButtons.class.getPackage().getName());
		// just in case the preference file permission is reset by
		// recovery/script
		pref.makeWorldReadable();

		// Initialize classes
		ModifyButtonsAction.initZygote(pref);
		try {
			ModifyNavigationBar.initZygote(pref);	
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		if (!resparam.packageName.equals(CLASSNAME_SYSTEMUI))
			return;

		modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
		
		resparam.res.setReplacement("com.android.systemui", "drawable", "ic_sysbar_home", modRes.fwd(R.drawable.ic_sysbar_home));
		resparam.res.setReplacement("com.android.systemui", "drawable", "ic_sysbar_recent", modRes.fwd(R.drawable.ic_sysbar_recent));
		resparam.res.setReplacement("com.android.systemui", "drawable", "ic_sysbar_menu", modRes.fwd(R.drawable.ic_sysbar_menu));
		resparam.res.setReplacement("com.android.systemui", "drawable", "ic_sysbar_back", modRes.fwd(R.drawable.ic_sysbar_back));

		resparam.res.hookLayout(CLASSNAME_SYSTEMUI, "layout", "navigation_bar", new XC_LayoutInflated() {
			@Override
			public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
				int buttonsCount;
				int screenWidth;
				int screenHeight;
				int buttonWidth;

				pref.reload();

				String[] orderList = pref.getString("pref_order", DEF_BUTTONS_ORDER_LIST).split(",");
				buttonsCount = orderList.length;

				mContext = liparam.view.getContext();
				final Display defaultDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				final Point point = new Point();
				defaultDisplay.getSize(point);
				screenWidth = point.x;
				screenHeight = point.y;

				Configuration config = mContext.getResources().getConfiguration();
				if (config.orientation == Configuration.ORIENTATION_PORTRAIT) { 
					buttonWidth = Math.round((float) screenWidth / (float) buttonsCount);
				} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
					buttonWidth = Math.round((float) screenHeight / (float) buttonsCount);
				} else {
					buttonWidth = Math.round((float) screenWidth / (float) buttonsCount);
				}
				XposedBridge.log(String.format("screenWidth:%d, screenHeight:%d, buttonWidth:%d", screenWidth, screenHeight, buttonWidth));

				FrameLayout rot0 = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("rot0", "id", CLASSNAME_SYSTEMUI));
				FrameLayout rot90 = (FrameLayout) liparam.view.findViewById(liparam.res.getIdentifier("rot90", "id", CLASSNAME_SYSTEMUI));
				LinearLayout rot0NavButtons = null;
				LinearLayout rot0LightsOut = null;
				LinearLayout rot0LightsOutHigh = null;
				LinearLayout rot90NavButtons = null;
				LinearLayout rot90LightsOut = null;
				LinearLayout rot90LightsOutHigh = null;

				// portrait views
				if (rot0 != null) {
					rot0NavButtons = (LinearLayout) rot0.findViewById(liparam.res.getIdentifier("nav_buttons", "id", CLASSNAME_SYSTEMUI));
					rot0LightsOut = (LinearLayout) rot0.findViewById(liparam.res.getIdentifier("lights_out", "id", CLASSNAME_SYSTEMUI));
					rot0LightsOutHigh = (LinearLayout) rot0.findViewById(liparam.res.getIdentifier("lights_out_high", "id", CLASSNAME_SYSTEMUI));

					// handle nav buttions
					if (rot0NavButtons != null) {
						Map<String, View> viewList = new HashMap<String, View>();
						viewList.put("Back", (View) rot0NavButtons.findViewById(liparam.res.getIdentifier("back", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Home", (View) rot0NavButtons.findViewById(liparam.res.getIdentifier("home", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Recent", (View) rot0NavButtons.findViewById(liparam.res.getIdentifier("recent_apps", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Menu", (View) rot0NavButtons.findViewById(liparam.res.getIdentifier("menu", "id", CLASSNAME_SYSTEMUI)));
						viewList.put(
								"Search",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_search, KeyEvent.KEYCODE_SEARCH, "search"));
						viewList.put(
								"Power",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_power, KeyEvent.KEYCODE_POWER, "power"));
						viewList.put(
								"Expand",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_expand, BUTTONACTION_STATUSBAR_ACTION, "expand"));
						viewList.put(
								"Custom",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_custom, BUTTONACTION_CUSTOM_ACTION, "custom"));
						viewList.put(
								"KillApp",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_killapp, BUTTONACTION_KILLAPP_ACTION, "killapp"));
						viewList.put(
								"Space",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight",
										R.drawable.ic_sysbar_space, BUTTONACTION_DO_NOTHING, "space"));

						rot0NavButtons.removeAllViews();
						// add selected buttons
						for (int i = 0; i < buttonsCount; i++) {
							View view = viewList.remove(orderList[i]);
							if (view != null) {
								view.setVisibility(View.VISIBLE);
								view.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, 0.0f));
								view.setPadding(0, 0, 0, 0);
								rot0NavButtons.addView(view);
							}
						}

						// add unselected buttons and make them invisible
						for (View view : viewList.values()) {
							view.setVisibility(View.GONE);
							rot0NavButtons.addView(view);
						}
					}

					// handle lights out
					if (rot0LightsOut != null) {
						rot0LightsOut.removeAllViews();
						int i = 0;
						while (i < buttonsCount) {
							rot0LightsOut.addView(createLightsOutView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT,
									"ic_sysbar_lights_out_dot_small"));
							i++;
						}
					}

					// handle lights out high
					if (rot0LightsOutHigh != null) {
						rot0LightsOutHigh.removeAllViews();
						int i = 0;
						while (i < buttonsCount) {
							rot0LightsOutHigh.addView(createLightsOutView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT,
									"ic_sysbar_lights_out_dot_small_high"));
							i++;
						}
					}
				}

				// landscape views
				if (rot90 != null) {
					rot90NavButtons = (LinearLayout) rot90.findViewById(liparam.res.getIdentifier("nav_buttons", "id", CLASSNAME_SYSTEMUI));
					rot90LightsOut = (LinearLayout) rot90.findViewById(liparam.res.getIdentifier("lights_out", "id", CLASSNAME_SYSTEMUI));
					rot90LightsOutHigh = (LinearLayout) rot90.findViewById(liparam.res.getIdentifier("lights_out_high", "id", CLASSNAME_SYSTEMUI));

					// handle nav buttions
					if (rot90NavButtons != null) {
						// determine if a tablet is in use
						boolean tabletMode = rot90NavButtons.getOrientation() == LinearLayout.HORIZONTAL;

						if (tabletMode) {
							rot90NavButtons.setGravity(Gravity.CENTER_HORIZONTAL);
						}
						Map<String, View> viewList = new HashMap<String, View>();
						viewList.put("Back", (View) rot90NavButtons.findViewById(liparam.res.getIdentifier("back", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Home", (View) rot90NavButtons.findViewById(liparam.res.getIdentifier("home", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Recent", (View) rot90NavButtons.findViewById(liparam.res.getIdentifier("recent_apps", "id", CLASSNAME_SYSTEMUI)));
						viewList.put("Menu", (View) rot90NavButtons.findViewById(liparam.res.getIdentifier("menu", "id", CLASSNAME_SYSTEMUI)));
						viewList.put(
								"Search",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_search_land, KeyEvent.KEYCODE_SEARCH, "search"));
						viewList.put(
								"Power",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_power_land, KeyEvent.KEYCODE_POWER, "power"));
						viewList.put(
								"Expand",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_expand, BUTTONACTION_STATUSBAR_ACTION, "expand"));
						viewList.put(
								"Custom",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_custom, BUTTONACTION_CUSTOM_ACTION, "custom"));
						viewList.put(
								"KillApp",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_killapp, BUTTONACTION_KILLAPP_ACTION, "killapp"));
						viewList.put(
								"Space",
								createButtonView(liparam, buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, "ic_sysbar_highlight_land",
										R.drawable.ic_sysbar_space, BUTTONACTION_DO_NOTHING, "space"));

						rot90NavButtons.removeAllViews();
						// add selected buttons
						if (tabletMode) {
							for (int i = 0; i < buttonsCount; i++) {
								View view = viewList.remove(orderList[i]);
								if (view != null) {
									view.setVisibility(View.VISIBLE);
									view.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.FILL_PARENT, 0.0f));
									view.setPadding(0, 0, 0, 0);
									rot90NavButtons.addView(view);
								}
							}
						} else {
							for (int i = buttonsCount - 1; i >= 0; i--) {
								View view = viewList.remove(orderList[i]);
								if (view != null) {
									view.setVisibility(View.VISIBLE);
									view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, buttonWidth, 0.0f));
									rot90NavButtons.addView(view);
								}
							}
						}

						// add unselected buttons and make them invisible
						for (View view : viewList.values()) {
							view.setVisibility(View.GONE);
							rot90NavButtons.addView(view);
						}
					}

					// handle lights out
					if (rot90LightsOut != null) {
						rot90LightsOut.removeAllViews();
						int i = 0;
						while (i < buttonsCount) {
							rot90LightsOut.addView(createLightsOutView(liparam, LinearLayout.LayoutParams.FILL_PARENT, buttonWidth,
									"ic_sysbar_lights_out_dot_small"));
							i++;
						}
					}

					// handle lights out high
					if (rot90LightsOutHigh != null) {
						rot90LightsOutHigh.removeAllViews();
						int i = 0;
						while (i < buttonsCount) {
							rot90LightsOutHigh.addView(createLightsOutView(liparam, LinearLayout.LayoutParams.FILL_PARENT, buttonWidth,
									"ic_sysbar_lights_out_dot_small_high"));
							i++;
						}
					}
				}

				XposedBridge.log(String.format("rot0:%b, rot0NavButtons:%b, rot0LightsOut:%b, rot0LightsOutHigh:%b", rot0 != null, rot0NavButtons != null,
						rot0LightsOut != null, rot0LightsOutHigh != null));
				XposedBridge.log(String.format("rot90:%b, rot90NavButtons:%b, rot90LightsOut:%b, rot90LightsOutHigh:%b", rot90 != null,
						rot90NavButtons != null, rot90LightsOut != null, rot90LightsOutHigh != null));
			}
		});
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(CLASSNAME_SYSTEMUI))
			return;

		// replace setDisabledFlags method
		try {
			XposedHelpers.findMethodExact(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setDisabledFlags", int.class, boolean.class);

			XposedHelpers.findAndHookMethod(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setDisabledFlags", int.class, boolean.class,
					new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// keep original value for afterHookedMethod
							mDisabledFlags = XposedHelpers.getIntField(param.thisObject, "mDisabledFlags");
						}

						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							final int disabledFlags = (Integer) param.args[0];
							final boolean force = (Boolean) param.args[1];

							final View mCurrentView = (View) XposedHelpers.getObjectField(param.thisObject, "mCurrentView");

							final View homeButton = mCurrentView.findViewById(mCurrentView.getResources().getIdentifier("home", "id", CLASSNAME_SYSTEMUI));
							final View backButton = mCurrentView.findViewById(mCurrentView.getResources().getIdentifier("back", "id", CLASSNAME_SYSTEMUI));
							final View recentButton = mCurrentView.findViewById(mCurrentView.getResources().getIdentifier("recent_apps", "id", CLASSNAME_SYSTEMUI));
							final View searchButton = mCurrentView.findViewWithTag("search");
							final View menuButton = mCurrentView.findViewById(mCurrentView.getResources().getIdentifier("menu", "id", CLASSNAME_SYSTEMUI));
							final View powerButton = mCurrentView.findViewWithTag("power");
							final View expandButton = mCurrentView.findViewWithTag("expand");
							final View customButton = mCurrentView.findViewWithTag("custom");
							final View killappButton = mCurrentView.findViewWithTag("killapp");
							final View spaceButton = mCurrentView.findViewWithTag("space");

							if (!force && mDisabledFlags == disabledFlags)
								return;

							final boolean disableRecent = (disabledFlags & View.STATUS_BAR_DISABLE_RECENT) != 0;

							if (homeButton != null)
								homeButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (backButton != null)
								backButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (searchButton != null)
								searchButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (menuButton != null)
								menuButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (recentButton != null)
								recentButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (powerButton != null)
								powerButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);
							if (expandButton != null)
								expandButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);	
							if (customButton != null)
								customButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);	
							if (killappButton != null)
								killappButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);	
							if (spaceButton != null)
								spaceButton.setVisibility(disableRecent ? View.INVISIBLE : View.VISIBLE);									
						}
					});
		} catch (NoSuchMethodError e2) {
			XposedBridge.log("setDisabledFlags not found");
			return;
		}

		// replace setMenuVisibility(boolean) method
		try {
			XposedHelpers.findMethodExact(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setMenuVisibility", boolean.class);

			XposedHelpers.findAndHookMethod(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setMenuVisibility", boolean.class, new XC_MethodReplacement() {
				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
					return null;
				}
			});
		} catch (NoSuchMethodError e2) {
			XposedBridge.log("setMenuVisibility(boolean) not found");
			return;
		}

		// replace setMenuVisibility(boolean, boolean) method
		try {
			XposedHelpers.findMethodExact(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setMenuVisibility", boolean.class, boolean.class);

			XposedHelpers.findAndHookMethod(CLASSNAME_NAVIGATIONBARVIEW, lpparam.classLoader, "setMenuVisibility", boolean.class, boolean.class,
					new XC_MethodReplacement() {
						@Override
						protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
							return null;
						}
					});
		} catch (NoSuchMethodError e2) {
			XposedBridge.log("setMenuVisibility(boolean, boolean) not found");
			return;
		}
	}

	ImageView createLightsOutView(LayoutInflatedParam liparam, int width, int height, String imgResName) {
		ImageView iv = new ImageView(mContext);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height, 0.0f);
		iv.setLayoutParams(lp);
		iv.setImageDrawable(liparam.res.getDrawable(liparam.res.getIdentifier(imgResName, "drawable", CLASSNAME_SYSTEMUI)));
		iv.setScaleType(ScaleType.CENTER);

		return iv;
	}

	KeyButtonView createButtonView(LayoutInflatedParam liparam, int width, int height, String glowBgResName, int imgResId, int code, String tag) {
		KeyButtonView view;
		if(code == KeyEvent.KEYCODE_POWER){
			view = new KeyButtonView(mContext, code, false, liparam.res.getDrawable(liparam.res.getIdentifier(glowBgResName, "drawable", CLASSNAME_SYSTEMUI)));
		}else{
			view = new KeyButtonView(mContext, code, true, liparam.res.getDrawable(liparam.res.getIdentifier(glowBgResName, "drawable", CLASSNAME_SYSTEMUI)));			
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height, 0.0f);
		view.setLayoutParams(lp);
		view.setImageDrawable(modRes.getDrawable(imgResId));
		view.setScaleType(ScaleType.CENTER);
		view.setTag(tag);
		return view;
	}
}
