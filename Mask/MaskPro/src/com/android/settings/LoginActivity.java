package com.android.settings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import cn.bmob.v3.listener.SaveListener;

import com.android.mask.MainActivity1;
import com.android.mask.R;
import com.bean._User;
import com.util.ToastFactory;

public class LoginActivity extends Activity {
	private Button LoginBtn;
	private Button Forget;
	private Button RegBtn;
	private EditText Addresss;
	private EditText psw_;
	private Context Login_Context;
	private Context Reg_Context;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_or_register);

		Addresss = (EditText) findViewById(R.id.accountEt_login);
		psw_ = (EditText) findViewById(R.id.pwdEt_login);
		Reg_Context = this;
		Login_Context = this;
		RegBtn = (Button) findViewById(R.id.Register_Btn);
		RegBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Reg_Context, RegisterActivity.class);
				finish();
				startActivity(intent);
				// TODO Auto-generated method stub
			}
		});

		LoginBtn = (Button) findViewById(R.id.LoginBtn);
		LoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email_text = Addresss.getText().toString();
				String psw_text = psw_.getText().toString();

				// 添加云端登录验证
				_User user = new _User();
				user.setUsername(email_text);
				user.setPassword(psw_text);

				user.login(LoginActivity.this, new SaveListener() {

				@Override
				public void onFailure(int arg0, String arg1) {
					ToastFactory.showToast(LoginActivity.this,// 提示浮框同时显示查询数据
							"登陆失败");
				}

				@Override
				public void onSuccess() {
					ToastFactory.showToast(LoginActivity.this,// 提示浮框同时显示查询数据
							"登陆成功");
				Intent intent=new Intent();
				intent.setClass(Login_Context, MainActivity1.class);
				startActivity(intent);
				LoginActivity.this.finish();
				}
				});
			}
		});

		Forget = (Button) findViewById(R.id.forget_Btn);
		Forget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forgetPwd();
			}
		});
	}

	protected void forgetPwd() {
		final String email = Addresss.getText().toString();
		if (email==null || email.equals("")) {
			ToastFactory.showToast(Login_Context, "请先填写邮箱");
		} else {
			BmobUser.resetPasswordByEmail(Login_Context, email, new ResetPasswordByEmailListener() {
				@Override
				public void onSuccess() {
					ToastFactory.showToast(Login_Context,"忘记密码，请到" + email + "邮箱进行密码重置操作");
					
				}
				@Override
				public void onFailure(int arg0, String arg1) {
					ToastFactory.showToast(Login_Context,"重置密码请求失败");
				}
			});
		}
	}
		
}
