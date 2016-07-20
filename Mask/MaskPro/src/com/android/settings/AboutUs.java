package com.android.settings;

import com.android.mask.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


public class AboutUs extends Activity {
	Context mContext;
	private ImageView returnSetting;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //È¡ÏûÄ¬ÈÏtop bar
		setContentView(R.layout.about_us); 
		
		mContext = this;
		returnSetting = (ImageView) findViewById(R.id.id_img_returnSetting);
		returnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				AboutUs.this.finish();
			}
		});
	}
}
