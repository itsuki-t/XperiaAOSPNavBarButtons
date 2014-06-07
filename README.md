#Xperia/AOSP NavBar Buttons
This project forked from [itandy's original project.](https://github.com/itandy/XperiaAOSPNavBarButtons "itandy/XperiaAOSPNavBarButtons")

This Xposed module allows you to change the NavigationBar buttons on your Xperia or AOSP devices.  
On stock ROM, there're only three buttons on the NavigationBar (Back, Home and Recent Apps).  
Now you can add/remove the various buttons and reorder.

##Installation instructions:
1. Install the module
2. Run Xposed Installer and enable the module "Xperia/AOSP NavBar Buttons"
3. Reboot the phone

##Features:
1. Add and remove, reorder those button. (max six buttons)  
 - Home
 - Back
     * LongPress : customizable
 - Recent App
 - Search
 - Menu
 - Power (same as the physical power button)
     * Press : screen off
     * LongPress : show power menu
 - Expand
     * Press : expand notification panel
     * LongPress : customizable
 - Custom
     * Press : launch app you have selected
	 * LongPress : customizable
 - Kill App
     * Press : kill foreground app  
     * LongPress : kill all app (same as "clear all" from recent app panel)  
 - Space
     * No action and no image button
2. Change NavigationBar height
3. Change longpress delay
4. Modify longpress action (select those action).
 - No action.
 - Expand notification panel.
 - Collapse notification panel.
 - Launch custom app.
 - Kill foreground app.
 - Kill all app.
 - Launch previous app.

## Change log:
* Ver 1.8
 - Add option to modify button longpress delay.
 - Add option to modify button longpress action.
 - Change statusbar expand button image.
 - Add help button on actionbar in Button Settings screen. (under development)
 - Fixed bug show navigationbar button when show power menu in lockscreen.
 - Fixed bug show power menu twice when longpress power button.
* Ver 1.7
 - Add kill foreground/all app button.
 - Add custom app icon to setting screen.
* Ver 1.6
 - Add cutsom button and option.
* Ver 1.5
 - Add space button.
* Ver 1.4
 - Add expand/collapse nortification panel button.
 - Change Settings screen.
 - Change reorder screen.
* Ver 1.3
 - Add feature to change Navigationbar height.
* Ver 1.2 (forked from itandy's original project)
 - Add option to show or hide recent app button.
 - Add power button.
 - Change Settings screen.
* Ver 1.1
 -  	Add support for MUCH i5s.
* Ver 1.0
 - Project release.
