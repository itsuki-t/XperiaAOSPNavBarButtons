package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ButtonSettings {
	private Drawable mImgHomeButton;
	private Drawable mImgBackButton;
	private Drawable mImgRecentButton;
	private Drawable mImgMenuButton;
	private Drawable mImgSearchButton;
	private Drawable mImgPowerButton;
	private Drawable mImgExpandButton;

	private boolean mShowMenu = false;
	private boolean mShowSearch = false;
	private boolean mShowRecent = false;
	private boolean mShowPower = false;
	private boolean mShowExpand = false;
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

	public String getInitOrderListString() {
		StringBuilder addedList = new StringBuilder();
		int i=0;
		boolean mHomeInc = false;
		boolean mBackInc = false;
		boolean mRecentInc = false;
		boolean mMenuInc = false;
		boolean mSearchInc = false;
		boolean mPowerInc = false;
		boolean mExpandInc = false;

		for (i = 0; i < mOrder.size(); i++) {
			addedList.append(mOrder.get(i));	
			if (mOrder.get(i).equalsIgnoreCase("Home")) {
				mHomeInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Back")){
				mBackInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Recent")){
				mRecentInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Menu")){
				mMenuInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Search")){
				mSearchInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Power")){
				mPowerInc = true;
			}else if (mOrder.get(i).equalsIgnoreCase("Expand")){
				mExpandInc = true;
			}
			if (i != mOrder.size() - 1)
				addedList.append(",");
		}
		if (!mHomeInc) {
			addedList.append(",");
			addedList.append("Home");
		}
		if (!mBackInc){
			addedList.append(",");
			addedList.append("Back");
		}
		if (!mRecentInc){
			addedList.append(",");
			addedList.append("Recent");
		}
		if (!mMenuInc){
			addedList.append(",");
			addedList.append("Menu");
		}
		if (!mSearchInc){
			addedList.append(",");
			addedList.append("Search");
		}
		if (!mPowerInc){
			addedList.append(",");
			addedList.append("Power");
		}
		if (!mExpandInc){
			addedList.append(",");
			addedList.append("Expand");
		}
		
		return addedList.toString();
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
		
		return null;
	}
}
