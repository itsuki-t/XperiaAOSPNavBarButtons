package com.gzplanet.xposed.xperianavbarbuttons;

import android.content.res.Resources;
import android.content.res.XResources;
import android.view.Display;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ModifyNavigationBar {
	private static final String TAG = "ModifyNavigationBar";
	
	public static void initZygote(final XSharedPreferences prefs) {
		// Change Navigationbar Height
		try {
			final int navBarHeight = Integer.valueOf(prefs.getString("nh_list_preference", "48"));

			if(navBarHeight != 48) {
				int navbarSizeId = getNavBarHeightId(navBarHeight);
				XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height", XperiaNavBarButtons.modRes.fwd(navbarSizeId));
				XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height_landscape", XperiaNavBarButtons.modRes.fwd(navbarSizeId));
			}
		} catch (Exception e) {
			XposedBridge.log(e);
		}
	}
 
	private static int getNavBarHeightId(int navBarHeight) {
		int navbarSizeId;

		switch(navBarHeight) {  
        case 52:   
        	navbarSizeId = R.dimen.navigation_bar_height_52; 
            break;  
  
        case 48:   
        	navbarSizeId = R.dimen.navigation_bar_height_48;  
            break;  

        case 44:   
        	navbarSizeId = R.dimen.navigation_bar_height_44;   
            break;  

        case 40:   
        	navbarSizeId = R.dimen.navigation_bar_height_40;  
            break;
            
        case 36:   
        	navbarSizeId = R.dimen.navigation_bar_height_36;   
            break;  
    
        case 32:   
        	navbarSizeId = R.dimen.navigation_bar_height_32;   
            break;  

        case 28:   
        	navbarSizeId = R.dimen.navigation_bar_height_28;   
            break;   
              
        case 24:   
        	navbarSizeId = R.dimen.navigation_bar_height_24;   
            break;
            
        case 20:
        	navbarSizeId = R.dimen.navigation_bar_height_20;
            break;
  
        default:  
        	navbarSizeId = R.dimen.navigation_bar_height_48;
        	break;
        }
		
		return navbarSizeId;
	}
}