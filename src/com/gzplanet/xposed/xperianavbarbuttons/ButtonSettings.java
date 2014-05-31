package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.ArrayList;

import com.gzplanet.xposed.xperianavbarbuttons.Buttons.NavBarButton;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ButtonSettings {
	@SuppressWarnings("unused")
	private static final String TAG = "ButtonSettings";
	
	ButtonSettings mSettings;
	NavBarButton mNavBarButton;
	private ArrayList<String> mOrder = new ArrayList<String>();

	public ButtonSettings(Context context, String orderList) {
		// Create Button class
		mNavBarButton = new NavBarButton(context);
		
		// prepare preview panel
		mNavBarButton.createButton("Home",1);
		mNavBarButton.createButton("Back",1);
		mNavBarButton.createButton("Recent",1);
		mNavBarButton.createButton("Menu",1);
		mNavBarButton.createButton("Search",2);
		mNavBarButton.createButton("Power",2);
		mNavBarButton.createButton("Expand",2);
		mNavBarButton.createButton("Custom",2);
		mNavBarButton.createButton("KillApp",2);
		mNavBarButton.createButton("Space",2);

		if (orderList == null) {
			mOrder.add("Home");
			mOrder.add("Menu");
			mOrder.add("Recent");
			mOrder.add("Back");
			mOrder.add("Search");
			mOrder.add("Power");
			mNavBarButton.SetUseFlag("Menu");
			mNavBarButton.SetUseFlag("Search");			
			mNavBarButton.SetUseFlag("Power");			
		} else {
			String[] array = orderList.split(",");
			for (int i = 0; i < array.length; i++) {
				mOrder.add(array[i]);
				mNavBarButton.SetUseFlag(array[i]);
			}
		}
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

		return mNavBarButton.getButtonImage(mOrder.get(index));
	}
}
