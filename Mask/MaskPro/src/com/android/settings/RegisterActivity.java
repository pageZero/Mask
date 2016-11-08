package com.android.settings;

import cn.bmob.v3.listener.SaveListener;

import com.android.mask.R;
import com.bean._User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private Button Register;
	private EditText email;
	private EditText psw;
	private EditText con_pwd;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.register_layout);

		email = (EditText) findViewById(R.id.email);
		psw = (EditText) findViewById(R.id.pwdEt_reg);
		con_pwd = (EditText) findViewById(R.id.confirm_pwdEt);
		Register = (Button) findViewById(R.id.Register);
		Register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String con_pwd_text = con_pwd.getText().toString();
				String pwd_text = psw.getText().toString();
				String email_text = email.getText().toString();
				
				// 判断是否没有输入
				if (con_pwd_text.equals("") || con_pwd_text.equals("")
						|| email_text.equals("")) {
					Toast.makeText(RegisterActivity.this,// 提示浮框同时显示查询数据
							"请输入基本信息", Toast.LENGTH_SHORT).show();
					return;
				}
				// 判断两次输入密码是否一致
				if (!pwd_text.equals(con_pwd_text)) {
					
					Toast.makeText(RegisterActivity.this,// 提示浮框同时显示查询数据
							"两次输入的密码不一致"+con_pwd_text+pwd_text, Toast.LENGTH_SHORT).show();
					return;
				}

				// 生成一个用户实例
				_User user = new _User();
				user.setUsername(email_text);//可以将用户名设置为邮箱地址
				user.setPassword(pwd_text);
				user.setEmail(email_text);
				//用户注册不用save方法
				user.signUp(RegisterActivity.this, new SaveListener() {

					@Override
					public void onFailure(int arg0, String arg1) {
						Toast.makeText(RegisterActivity.this,// 提示浮框同时显示查询数据
								"注册失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess() {
						Toast.makeText(RegisterActivity.this,// 提示浮框同时显示查询数据
								"注册成功", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(RegisterActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				});
			}
		});
	}
}
