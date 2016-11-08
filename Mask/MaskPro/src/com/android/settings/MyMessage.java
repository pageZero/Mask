package com.android.settings;

import com.android.mask.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyMessage extends Activity {

	private TextView title;// top_bar上面的文字提示
	private RelativeLayout return_settings_center;//返回按钮
	private Context mymessage_context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.my_message);
		init();
		setListener();
	}
	
	private void init() {
		mymessage_context = this;
		title = (TextView) findViewById(R.id.top_bar_title);
		title.setText("消息通知");
		return_settings_center = (RelativeLayout) findViewById(R.id.return_layout);
		
	}
	private void setListener() {
		return_settings_center.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mymessage_context, SettingsCenterActivity.class);
				finish();
				startActivity(intent);
			}
		});
		
	}
}
