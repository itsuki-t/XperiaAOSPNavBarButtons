package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ButtonSettings {
	private static final String TAG = "ButtonSettings";
	private Drawable mImgHomeButton;
	private Drawable mImgBackButton;
	private Drawable mImgRecentButton;
	private Drawable mImgMenuButton;
	private Drawable mImgSearchButton;
	private Drawable mImgPowerButton;
	private Drawable mImgExpandButton;
	private Drawable mImgCustomButton;
	private Drawable mImgKillAppButton;
	private Drawable mImgSpaceButton;

	private boolean mShowMenu = false;
	private boolean mShowSearch = false;
	private boolean mShowRecent = false;
	private boolean mShowPower = false;
	private boolean mShowExpand = false;
	private boolean mShowCustom = false;
	private boolean mShowKillApp = false;
	private boolean mShowSpace = false;
	private ArrayList<String> mOrder = new ArrayList<String>();

	public ButtonSettings(Context context, String orderList) {
		// prepare preview panel
		PackageManager pm = context.getPackageManager();
		try {
			Resources resSystemUI = pm.getResourcesForApplication(XperiaNavBarButtons.CLASSNAME_SYSTEMUI);
			mImgHomeButton = resSystemUI.getDrawable(resSystemUI.getIdentifier("ic_sysbar_home", "drawable", XperiaNavBarButtons.CLASSNAME_SYSTEMUI));
			mImgBackButton = resSystemUI.getDrawable(resSystemUI.getIdentifier("ic_sysbar_back", "drawable", XperiaNavBarButtons.CLASSNAME_SYSTEMUI));
			mImgRecentButton = resSystemUI.getDrawable(resSystemUI.getIdentifier("ic_sysbar_recent", "drawable", XperiaNavBarButtons.CLASSNAME_SYSTEMUI));
			mImgMenuButton = resSystemUI.getDrawable(resSystemUI.getIdentifier("ic_sysbar_menu", "drawable", XperiaNavBarButtons.CLASSNAME_SYSTEMUI));
			mImgSearchButton = context.getResources().getDrawable(R.drawable.ic_sysbar_search);
			mImgPowerButton = context.getResources().getDrawable(R.drawable.ic_sysbar_power);
			mImgExpandButton = context.getResources().getDrawable(R.drawable.ic_sysbar_expand);
			mImgCustomButton = context.getResources().getDrawable(R.drawable.ic_sysbar_custom);
			mImgKillAppButton = context.getResources().getDrawable(R.drawable.ic_sysbar_killapp);
			mImgSpaceButton = context.getResources().getDrawable(R.drawable.ic_sysbar_space);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		if (orderList == null) {
			mOrder.add("Home");
			mOrder.add("Menu");
			mOrder.add("Recent");
			mOrder.add("Back");
			mOrder.add("Search");
			mOrder.add("Power");
			mShowMenu = true;
			mShowSearch = true;
			mShowPower = true;
			mShowExpand = false;
			mShowCustom = false;
			mShowKillApp = false;
			mShowSpace = false;
		} else {
			String[] array = orderList.split(",");
			for (int i = 0; i < array.length; i++) {
				mOrder.add(array[i]);

				if ("Menu".equals(array[i]))
					mShowMenu = true;
				if ("Search".equals(array[i]))
					mShowSearch = true;
				if ("Recent".equals(array[i]))
					mShowRecent = true;
				if ("Power".equals(array[i]))
					mShowPower = true;
				if ("Expand".equals(array[i]))
					mShowExpand = true;
				if ("Custom".equals(array[i]))
					mShowCustom = true;
				if ("Kill App".equals(array[i]))
					mShowKillApp = true;
				if ("Space".equals(array[i]))
					mShowSpace = true;
			}
		}
	}

	public boolean isShowRecent() {
		return mShowRecent;
	}

	public void setShowRecent(boolean showRecent) {
		mShowRecent = showRecent;
		if (mShowRecent)
			addButton("Recent");
		else
			removeButton("Recent");
	}

	public boolean isShowMenu() {
		return mShowMenu;
	}

	public void setShowMenu(boolean showMenu) {
		mShowMenu = showMenu;
		if (mShowMenu)
			addButton("Menu");
		else
			removeButton("Menu");
	}

	public boolean isShowSearch() {
		return mShowSearch;
	}

	public void setShowSearch(boolean showSearch) {
		mShowSearch = showSearch;
		if (mShowSearch)
			addButton("Search");
		else
			removeButton("Search");
	}

	public boolean isShowPower() {
		return mShowPower;
	}

	public void setShowPower(boolean showPower) {
		mShowPower = showPower;
		if (mShowPower)
			addButton("Power");
		else
			removeButton("Power");
	}	

	public boolean isShowExpand() {
		return mShowExpand;
	}

	public void setShowExpand(boolean showExpand) {
		mShowExpand = showExpand;
		if (mShowExpand)
			addButton("Expand");
		else
			removeButton("Expand");
	}

	public boolean isShowCustom() {
		return mShowCustom;
	}

	public void setShowCustom(boolean showCustom) {
		mShowCustom = showCustom;
		if (mShowCustom)
			addButton("Custom");
		else
			removeButton("Custom");
	}	

	public boolean isShowKillApp() {
		return mShowKillApp;
	}

	public void setShowKillApp(boolean showKillApp) {
		mShowKillApp = showKillApp;
		if (mShowKillApp)
			addButton("Kill App");
		else
			removeButton("Kill App");
	}	
	
	public boolean isShowSpace() {
		return mShowSpace;
	}

	public void setShowSpace(boolean showSpace) {
		mShowSpace = showSpace;
		if (mShowSpace)
			addButton("Space");
		else
			removeButton("Space");
	}		
	
	private void removeButton(String button) {
		int pos = mOrder.indexOf(button);
		if (pos >= 0)
			mOrder.remove(pos);
	}

	private void addButton(String button) {
		int pos = mOrder.indexOf(button);
		if (pos == -1)
			mOrder.add(button);
	}

	public String getOrderListString() {
		StringBuilder list = new StringBuilder();

		for (int i = 0; i < mOrder.size(); i++) {
			list.append(mOrder.get(i));
			if (i != mOrder.size() - 1)
				list.append(",");
		}

		return list.toString();
	}
	
	public Drawable getButtonDrawable(int index) {
		if (index >= mOrder.size())
			return null;

		if ("Home".equals(mOrder.get(index)))
			return mImgHomeButton;

		if ("Back".equals(mOrder.get(index)))
			return mImgBackButton;

		if ("Recent".equals(mOrder.get(index)))
			return mImgRecentButton;

		if ("Menu".equals(mOrder.get(index)))
			return mImgMenuButton;

		if ("Search".equals(mOrder.get(index)))
			return mImgSearchButton;

		if ("Power".equals(mOrder.get(index)))
			return mImgPowerButton;

		if ("Expand".equals(mOrder.get(index)))
			return mImgExpandButton;

		if ("Custom".equals(mOrder.get(index)))
			return mImgCustomButton;

		if ("Kill App".equals(mOrder.get(index)))
			return mImgKillAppButton;

		if ("Space".equals(mOrder.get(index)))
			return mImgSpaceButton;
		
		return null;
	}
}
