package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.tlv.TouchListView;
import com.gzplanet.xposed.xperianavbarbuttons.Buttons.NavBarButton;

public class ButtonSettingsActivity extends ListActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "ButtonSettingsActivity";
	private IconicAdapter mAdapter = null;
	private ArrayList<String> mItems;
	NavBarButton mNavBarButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		getActionBar().setTitle("Button Settings");
		setContentView(R.layout.reorder);

		// Create Buttons
		mNavBarButton = new NavBarButton(getApplicationContext());
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
		Intent intent = getIntent();
		String[] items = intent.getStringExtra("order_list").split(",");
		mItems = new ArrayList<String>(Arrays.asList(items));
		TouchListView tlv = (TouchListView) getListView();
		mAdapter = new IconicAdapter();
		setListAdapter(mAdapter);
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		
		tlv.setAdapter(mAdapter); 
		tlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = mAdapter.getItem(position);
				String[] str = {"Home", "Recent", "Search", "Menu", "Power"};
				List list = Arrays.asList(str);
				if(!(list.contains(mItems.get(position)))){
					showButtonPressed(mItems.get(position),position,id);
				} else {
			    	Toast.makeText(ButtonSettingsActivity.this, getString(R.string.cant_modify_longpress, item), Toast.LENGTH_LONG).show();					
				}
			}
		});
		
		tlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String item = mAdapter.getItem(position);
		        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
		        MenuInflater inflater = popup.getMenuInflater();
	            inflater.inflate(R.menu.popup, popup.getMenu());
		        popup.show();
		        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            public boolean onMenuItemClick(MenuItem menuItem) {
		        		if ("Home".equals(item) || ("Back".equals(item))){
			                Toast.makeText(ButtonSettingsActivity.this, getString(R.string.button_delete_error, item),  Toast.LENGTH_SHORT).show();		        			
		        		}else{
		        			mAdapter.remove(item);
		        		}
		                return true;
		            }
		        });
		    	return true;
			}
		});	
	}

	public void showButtonPressed(final String buttonName, int position, long id) {
//		final CharSequence[] Items =  {"No Action","Open Google search","Open recent panel","Back","Return home","Menu open","Screen off","Expand notification panel","Collapse notification panel","Launch custom app","Kill foreground app"};
		final CharSequence[] Items =  {"No Action","Expand notification panel","Collapse notification panel","Launch custom app","Kill foreground app","Kill all app","Launch previous app"};
		final String prefLongpressOrgName = "pref_longpress_";
		final StringBuilder sb = new StringBuilder();
		final StringBuilder sbt = new StringBuilder();
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		AlertDialog.Builder adb = new AlertDialog.Builder(ButtonSettingsActivity.this);
		sbt.append(buttonName.toUpperCase());
		sbt.append(" LONGPRESS ACTION\n(REBOOT REQUIRED)");
		String diagTitle = sbt.toString();
		adb.setTitle(diagTitle);
		adb.setItems(Items, new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which) {
				sb.append(prefLongpressOrgName);
				sb.append(buttonName.toLowerCase().replaceAll(" ", ""));
				String prefLongpressName = sb.toString();
				sharedPreferences.edit().putString(prefLongpressName, String.format("%s",Items[which])).commit();
		    }});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){   
			public void onClick(DialogInterface dialog, int which) {
//				Toast.makeText(ButtonSettingsActivity.this, "Cancel", Toast.LENGTH_LONG).show();
			}});
		adb.setCancelable(false);
		adb.show();
	}	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addButtonOption = menu.add(0, 0, 0, "Add Button");
		MenuItem helpOption = menu.add(0, 1, 0, "Help");	    
		addButtonOption.setIcon(android.R.drawable.ic_menu_add);
		helpOption.setIcon(android.R.drawable.ic_menu_help);
		addButtonOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		helpOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			final CharSequence[] Items =  {"Search","Recent","Back","Home","Menu","Power","Expand","Custom","KillApp","Space"};
			AlertDialog.Builder sab = new AlertDialog.Builder(ButtonSettingsActivity.this);   
			sab.setTitle("Select add button");   
			sab.setItems(Items, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					boolean addFlg = true;
					for (int i = 0; i < mItems.size(); i++) {
						if (Items[which].equals(mItems.get(i))){
							addFlg = false;
						}
					}
					if(mItems.size() != 6){				
						if(addFlg){
							mAdapter.add((String)Items[which]);
						}else{
							Toast.makeText(ButtonSettingsActivity.this, getString(R.string.button_already_added_error, Items[which]), Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(ButtonSettingsActivity.this, getString(R.string.button_over_max_error), Toast.LENGTH_LONG).show();					
					}
				}
			});

			sab.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){   
				public void onClick(DialogInterface dialog, int which) {
					// Action for add button menu canceled
				}});
			sab.setCancelable(false);
			sab.show();

			return true;
		case 1:
			Toast.makeText(ButtonSettingsActivity.this, "Help pressed.", Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}
    
	@Override
	public void onBackPressed() {
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < mItems.size(); i++) {
			list.append(mItems.get(i));
			if (i != mItems.size() - 1)
				list.append(",");
		}

		Intent intent = new Intent();
		intent.putExtra("order_list", list.toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	private TouchListView.DropListener onDrop = new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = mAdapter.getItem(from);

			mAdapter.remove(item);
			mAdapter.insert(item, to);
		}
	};

	private TouchListView.RemoveListener onRemove = new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
			mAdapter.remove(mAdapter.getItem(which));
		}
	};

	class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(ButtonSettingsActivity.this, R.layout.sort_list_row, mItems);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			final String prefLongpressOrgName = "pref_longpress_";
			final StringBuilder sb = new StringBuilder();
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();

				row = inflater.inflate(R.layout.sort_list_row, parent, false);
			}
			TextView label = (TextView) row.findViewById(R.id.label);
			ImageView iv = (ImageView) row.findViewById(R.id.buttonimage);
			label.setText(mItems.get(position));
			iv.setImageDrawable(mNavBarButton.getButtonImage(mItems.get(position)));

			TextView longpress = (TextView) row.findViewById(R.id.longpress);
			String[] str = {"Home", "Recent", "Search", "Menu", "Power"};
			List list = Arrays.asList(str);
			if(!(list.contains(mItems.get(position)))){
				longpress = (TextView) row.findViewById(R.id.longpress);
				sb.append(prefLongpressOrgName);
				sb.append(mItems.get(position).toLowerCase().replaceAll(" ", ""));
				String prefName = sb.toString();
				String longPressAction = sharedPreferences.getString(prefName,"");
				if(longPressAction != ""){
					longpress.setText("LONGPRESS : "+longPressAction);
				} else {
					longpress.setText("LONGPRESS : No Action");
				}
			} else {
				longpress.setHeight(0);
				longpress.setVisibility(View.INVISIBLE);
			}			
			return (row);
		}
	}
}
