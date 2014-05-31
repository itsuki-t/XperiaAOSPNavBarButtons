package com.gzplanet.xposed.xperianavbarbuttons.Buttons.CustomButton;

import java.util.List;

import com.gzplanet.xposed.xperianavbarbuttons.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDataAdapter extends ArrayAdapter<AppData> {
	@SuppressWarnings("unused")
	private static final String TAG = "AppDataAdapter";
	private LayoutInflater layoutInflater_;

	public AppDataAdapter(Context context, int textViewResourceId, List<AppData> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppData item = (AppData) getItem(position);

		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.applistrow, null);
		}

		ImageView imageView;
		imageView = (ImageView) convertView.findViewById(R.id.imageView);
		imageView.setImageDrawable(item.getAppIcon());

		TextView textView;
		textView = (TextView) convertView.findViewById(R.id.textView);
		textView.setText(item.getAppName());

		TextView textView2;
		textView2 = (TextView) convertView.findViewById(R.id.textView2);
		textView2.setText(item.getAppPackageName());

		return convertView;
	}
}