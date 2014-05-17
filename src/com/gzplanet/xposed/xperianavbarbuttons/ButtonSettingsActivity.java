package com.gzplanet.xposed.xperianavbarbuttons;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

public class ButtonSettingsActivity extends ListActivity {
	private static final String TAG = "ButtonSettingsActivity";
	private IconicAdapter mAdapter = null;
	private ArrayList<String> mItems;
	private ArrayList<Boolean> mItemsUse;
	Drawable mImgHomeButton;
	Drawable mImgBackButton;
	Drawable mImgRecentButton;
	Drawable mImgMenuButton;
	Drawable mImgSearchButton;
	Drawable mImgPowerButton;
	Drawable mImgExpandButton;
	Drawable mImgCustomButton;
	Drawable mImgSpaceButton;
	Drawable mImgKillAppButton;
	Drawable mImgOpenNotificationButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		getActionBar().setTitle("Button Settings");
		setContentView(R.layout.reorder);

		setButtonImage();
		Intent intent = getIntent();
		String[] items = intent.getStringExtra("order_list").split(",");
		mItems = new ArrayList<String>(Arrays.asList(items));
		mItemsUse = new ArrayList<Boolean>();
		for (int i = 0; i < mItems.size(); i++) {
			mItemsUse.add(true);
		}
		TouchListView tlv = (TouchListView) getListView();
		mAdapter = new IconicAdapter();
		setListAdapter(mAdapter);
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
/*		
		tlv.setAdapter(mAdapter); 
		tlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自動生成されたメソッド・スタブ
				String item = mAdapter.getItem(position);
				showButtonPressed(position,id);
		    	Toast.makeText(ButtonSettingsActivity.this, String.format("%s selected", item), Toast.LENGTH_LONG).show();
			}
		});
*/		
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
/*
	public void showButtonPressed(int position, long id) {
		final CharSequence[] Items =  {"Single Press","Long Press","Double Press"};
		AlertDialog.Builder sbp = new AlertDialog.Builder(ButtonSettingsActivity.this);   
		sbp.setTitle("Change Button Action");   
		sbp.setItems(Items, new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which) {
		    	Toast.makeText(ButtonSettingsActivity.this, String.format("%s Selected", Items[which]), Toast.LENGTH_LONG).show();
		    }});
		sbp.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){   
			public void onClick(DialogInterface dialog, int which) {   
				Toast.makeText(ButtonSettingsActivity.this, "Cancel", Toast.LENGTH_LONG).show();   
			}});
		sbp.setCancelable(false);
		sbp.show();
	}	
*/	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem actionItem = menu.add("Add Button");
		actionItem.setIcon(android.R.drawable.ic_menu_add);
		actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		final CharSequence[] Items =  {"Search","Recent","Back","Home","Menu","Power","Custom","Kill App","Open Notifications"};
		final CharSequence[] Items =  {"Search","Recent","Back","Home","Menu","Power","Expand","Custom","Space"};
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
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();

				row = inflater.inflate(R.layout.sort_list_row, parent, false);
			}

			TextView label = (TextView) row.findViewById(R.id.label);
			ImageView iv = (ImageView) row.findViewById(R.id.buttonimage);
			label.setText(mItems.get(position));
			iv.setImageDrawable(getButtonDrawable(mItems.get(position)));

			return (row);
		}
	}

	private void setButtonImage(){
		mImgHomeButton = this.getResources().getDrawable(R.drawable.ic_sysbar_home);
		mImgBackButton = this.getResources().getDrawable(R.drawable.ic_sysbar_back);
		mImgRecentButton = this.getResources().getDrawable(R.drawable.ic_sysbar_recent);
		mImgMenuButton = this.getResources().getDrawable(R.drawable.ic_sysbar_menu);
		mImgSearchButton = this.getResources().getDrawable(R.drawable.ic_sysbar_search);
		mImgPowerButton = this.getResources().getDrawable(R.drawable.ic_sysbar_power);
		mImgExpandButton = this.getResources().getDrawable(R.drawable.ic_sysbar_expand);
		mImgCustomButton = this.getResources().getDrawable(R.drawable.ic_sysbar_custom);
		mImgSpaceButton = this.getResources().getDrawable(R.drawable.ic_sysbar_space);
		mImgKillAppButton = this.getResources().getDrawable(R.drawable.ic_sysbar_power);
		mImgOpenNotificationButton = this.getResources().getDrawable(R.drawable.ic_sysbar_power);
	}

	private Drawable getButtonDrawable(String buttonName) {
		if ("Home".equals(buttonName))
			return mImgHomeButton;

		if ("Back".equals(buttonName))
			return mImgBackButton;

		if ("Recent".equals(buttonName))
			return mImgRecentButton;

		if ("Menu".equals(buttonName))
			return mImgMenuButton;

		if ("Search".equals(buttonName))
			return mImgSearchButton;

		if ("Power".equals(buttonName))
			return mImgPowerButton;

		if ("Expand".equals(buttonName))
			return mImgExpandButton;

		if ("Custom".equals(buttonName))
			return mImgCustomButton;

		if ("Space".equals(buttonName))
			return mImgSpaceButton;

		if ("Kill App".equals(buttonName))
			return mImgKillAppButton;

		if ("Open Notifications".equals(buttonName))
			return mImgOpenNotificationButton;

		return null;
	}
}
