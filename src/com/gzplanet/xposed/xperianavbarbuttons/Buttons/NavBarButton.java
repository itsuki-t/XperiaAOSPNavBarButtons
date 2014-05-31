package com.gzplanet.xposed.xperianavbarbuttons.Buttons;

import java.util.HashMap;
import java.util.Map;

import com.gzplanet.xposed.xperianavbarbuttons.R;
import com.gzplanet.xposed.xperianavbarbuttons.XperiaNavBarButtons;

import de.robv.android.xposed.XposedBridge;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class NavBarButton {
	@SuppressWarnings("unused")
	private static final String TAG = "NavBarButton";
	private Context mContext;
	private Map<String, Drawable> buttonImageMap = new HashMap<String, Drawable>();
	private Map<String, Boolean> useFlagMap = new HashMap<String, Boolean>();

	public NavBarButton(Context context) {
		this.mContext = context;
    }
    
	public void createButton(String buttonName, Integer resourceType){
		buttonImageMap.put(buttonName, setButtonImage(buttonName, resourceType));
		useFlagMap.put(buttonName,false);
	}

	private Drawable setButtonImage(String buttonName, Integer resourceType){
		String commonImageFileName = "ic_sysbar_";
		StringBuilder sb = new StringBuilder();
		sb.append(commonImageFileName);
		sb.append(buttonName.toLowerCase());
		String imageFileName = sb.toString();
		Drawable buttonImage = null;
		int resId = 0;

		switch (resourceType){
		case 1:
			// Get from SystemUI
			try {
				Resources resSystemUI = mContext.getPackageManager().getResourcesForApplication(XperiaNavBarButtons.CLASSNAME_SYSTEMUI);
				buttonImage = resSystemUI.getDrawable(resSystemUI.getIdentifier(imageFileName, "drawable", XperiaNavBarButtons.CLASSNAME_SYSTEMUI));
			} catch (NameNotFoundException e) {
				XposedBridge.log(e);
			}
			break;
		case 2:
			// Get from this app
			resId = mContext.getResources().getIdentifier(imageFileName, "drawable", mContext.getPackageName());
			buttonImage = mContext.getResources().getDrawable(resId);
			break;
		default:
			//
			break;
		}
		return buttonImage;
	}

	public Drawable getButtonImage(String buttonName){
		return buttonImageMap.get(buttonName);
	}

	public void SetUseFlag(String buttonName){
		useFlagMap.put(buttonName,true);
	}
	
	Boolean getUseFlag(String buttonName){
		return useFlagMap.get(buttonName);
	}
	
	public Integer getValueOfFlagIsTrue(){
		int buttonsCount = 0;
		for ( String key : useFlagMap.keySet() ) {
			Boolean flag = useFlagMap.get(key);
			if(flag)
				buttonsCount++;
		}
		return buttonsCount;
	}
    
}