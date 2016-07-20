package com.myview;

import com.android.mask.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyProgressDialog extends Dialog{
	public static final int THEME_LIGHT = 0;
	public static final int THEME_BLACK = 1;
	public String msg;
	public int theme = THEME_BLACK;
	
	public MyProgressDialog(Context context) {
		super(context,R.style.CustomProgressDialog);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myview_progressdialog);
	
		TextView msgText = (TextView) findViewById(R.id.id_tv_loadingmsg);
		if(msg!=null){
			msgText.setText(msg);
		}
		if(theme==THEME_LIGHT){
			View p = (View) msgText.getParent();
			p.setBackgroundColor(getContext().getResources().getColor(R.color.transparent_bg));
			msgText.setTextColor(getContext().getResources().getColor(R.color.item_hr_color));
		}
	}
	/**
	 * 设置显示文本
	 */
	public void setMsg(String msg){
		this.msg = msg;
	}
	/**
	 * 设置背景主题
	 */
	public void setBgTheme(int theme){
		this.theme = theme;
	}
}