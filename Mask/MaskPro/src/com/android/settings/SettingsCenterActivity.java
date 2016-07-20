package com.android.settings;

import cn.bmob.v3.BmobUser;

import com.android.mask.MainActivity1;
import com.android.mask.R;
import com.android.recommend.RecommendActivity;
import com.android.topic.MyContentActivity;
import com.bean._User;
import com.util.ToastFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsCenterActivity extends Activity {

	private RelativeLayout personal_data;
	private RelativeLayout my_collection;
	private RelativeLayout mytopic;
	private RelativeLayout my_message;
	private RelativeLayout my_upload;
	private RelativeLayout contact_us;
	private RelativeLayout software_update;
	private RelativeLayout logout;

	_User userInfo;// 当前登录了的用户
	private TextView login_register_text;
	private ImageView btn_login_register;
	private ImageView topic;
	private Context btn_user_center_Context;
	private ImageView return_to_home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_center_layout);
		init();// 初始化需要用到的textView
		// 从缓存中获取当前登录的用户
		userInfo = BmobUser.getCurrentUser(this, _User.class);
		setListener();
		// 初始化页面显示
		if (userInfo != null) {// 如果有用户登录
			if (userInfo.getUserNick() == null)// 用户没有设置昵称的时候默认是女神，设置了昵称之后改为昵称
				login_register_text.setText("女神" + "(" + userInfo.getUsername()
						+ ")");
			else
				login_register_text.setText(userInfo.getUserNick().toString()
						+ "(" + userInfo.getUsername() + ")");
			// 显示用户头像
			if (userInfo.getHead() != null) {
				userInfo.getHead().loadImage(btn_user_center_Context,
						btn_login_register);
			}
		}
	}

	private void init() {
		personal_data = (RelativeLayout) findViewById(R.id.personal_data);
		my_collection = (RelativeLayout) findViewById(R.id.my_collection);
		my_message = (RelativeLayout) findViewById(R.id.my_message);
		mytopic = (RelativeLayout) findViewById(R.id.mytopic);
		my_upload = (RelativeLayout) findViewById(R.id.my_upload);
		contact_us = (RelativeLayout) findViewById(R.id.contact_us);
		software_update = (RelativeLayout) findViewById(R.id.software_update);
		logout = (RelativeLayout) findViewById(R.id.logout);

		btn_user_center_Context = this;
		btn_login_register = (ImageView) findViewById(R.id.id_img1);
		login_register_text = (TextView) findViewById(R.id.id_login_or_register);
		return_to_home = (ImageView) findViewById(R.id.return_right);
		return_to_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	/**
	 * 为每个按钮绑定监听事件
	 */
	private void setListener() {
		// 登录注册的按钮，就是显示头像的位置，如果没有用户登录，点击跳转到登录/注册
		// 否则跳转的个人资料页面
		btn_login_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userInfo == null) {
					Intent intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(btn_user_center_Context,
							PersonalData.class);
					startActivity(intent);
				}
			}
		});
		// 个人资料的按钮，如果有用户登录，跳转到个人资料页面
		// 否则跳转到登录注册
		personal_data.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
				} else {
					intent = new Intent(btn_user_center_Context,
							PersonalData.class);
				}
				startActivity(intent);
			}
		});
		// 我的帖子按钮，如果没有用户登录，跳转登录注册
		// 否则跳转到我的页面！！！

		mytopic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);

				} else {
					intent = new Intent(btn_user_center_Context,
							MyContentActivity.class);
				}
				startActivity(intent);
			}
		});

		// 我的收藏按钮，如果没有用户登录，跳转登录注册
		// 否则跳转到我的收藏页面！！！
		my_collection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
				} else {
					intent = new Intent(btn_user_center_Context,
							MyCollection.class);
				}
				startActivity(intent);
			}
		});
		// 消息通知按钮，如果没有用户登录，跳转登录注册
		// 否则跳转到消息通知页面！！！
		my_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
				} else {
					intent = new Intent(btn_user_center_Context,
							MyMessage.class);
				}
				startActivity(intent);
			}
		});
		my_upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
				} else {
					intent = new Intent(btn_user_center_Context,
							MyUpload.class);
				}
				startActivity(intent);
			}
		});
		// 联系我们：如果没有用户登录，跳转到登录/注册
		// 否则跳转到联系我们的页面
		contact_us.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (userInfo == null) {
					intent = new Intent(btn_user_center_Context,
							LoginActivity.class);
				} else {
					intent = new Intent(btn_user_center_Context,
							AboutUs.class);
				}
				startActivity(intent);
			}
		});

		// 软件升级：显示软件升级的信息
		software_update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastFactory.showToast(btn_user_center_Context, "目前已是最新版本！");
			}
		});

		// 退出登录：如果没有登录，提示请先登录
		// 否则清除useInfo的信息，并设置显示头像为背景me
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userInfo == null) {
					Toast.makeText(SettingsCenterActivity.this,// 提示浮框同时显示查询数据
							"请先登录", Toast.LENGTH_SHORT).show();
				} else {
					BmobUser.logOut(btn_user_center_Context);// 清除缓存用户对象
					userInfo = (_User) BmobUser
							.getCurrentUser(btn_user_center_Context); // 现在的currentUser是null了
					// btn_login_register.setImageBitmap(null);//setBackgroundResource(R.drawable.iv_background_me);
					btn_login_register.setImageResource(R.drawable.bigger_logo);
					login_register_text.setText("登录/注册");
					Intent intent=new Intent();
					intent.setClass(SettingsCenterActivity.this,MainActivity1.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

}
