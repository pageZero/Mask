package com.android.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.android.mask.R;
import com.bean._User;
import com.util.CacheUtils;
import com.util.ImageUtil;
import com.util.ToastFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalData extends Activity {

	private RelativeLayout return_settings_center;
	private TextView saveUpdate;// 保存修改，上传到云端
	private TextView title;// top_bar上面的文字提示
	private RelativeLayout head_change_layout;// 点击修改头像的视图
	private ImageView head;// 显示头像的视图
	private String iconUrl;
	_User currentUser;
	private Context personal_data_context;
	
	private TextView userName;//显示用户名的控件
	private EditText newNick;
	// 修改密码
	private RelativeLayout pwd_change_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.personal_data);
		init();
		setListener();
	}

	/**
	 * 初始化页面的各个控件
	 */
	private void init() {
		personal_data_context = this;
		currentUser = BmobUser.getCurrentUser(personal_data_context,_User.class);
		return_settings_center = (RelativeLayout) findViewById(R.id.return_layout);
		saveUpdate = (TextView) findViewById(R.id.save_personal_data_update);
		saveUpdate.setText("保存");
		title = (TextView) findViewById(R.id.top_bar_title);
		title.setText("修改资料");
		head_change_layout = (RelativeLayout) findViewById(R.id.change_head);
		head = (ImageView) findViewById(R.id.head);
		//如果登录的用户设置了头像，显示头像
		if (currentUser.getHead() != null) {
			currentUser.getHead().loadImage(personal_data_context, head);
		}
		userName = (TextView) findViewById(R.id.user_name);
		userName.setText(currentUser.getUsername());
		// 修改昵称的文本框
		newNick = (EditText) findViewById(R.id.nick);
		if(currentUser.getUserNick() != null) {
			newNick.setText(currentUser.getUserNick());
		} else {
			newNick.setHint("女神");
		}
		pwd_change_layout = (RelativeLayout) findViewById(R.id.change_pwd_layout);
	}
	/**
	 * 修改头像
	 */
	String dateTime;
	AlertDialog albumDialog;
	public void showAlbumDialog() {
		//弹出选择图片方式对话框
		albumDialog = new AlertDialog.Builder(personal_data_context).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(personal_data_context).inflate(R.layout.myview_dialog_usericon, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
		//图库选择
		albumPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				albumDialog.dismiss();
				Date date1 = new Date(System.currentTimeMillis());
				dateTime = date1.getTime() + "";
				getAvataFromAlbum();
			}
		});
		//拍照选择
		cameraPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				albumDialog.dismiss();
				Date date = new Date(System.currentTimeMillis());
				dateTime = date.getTime() + "";
				getAvataFromCamera();
			}
		});
	}
	/**
	 * 跳转到图库
	 */
	private void getAvataFromAlbum() {
		Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
		intent2.setType("image/*");
		startActivityForResult(intent2, 2);
	}
	/**
	 * 跳转到相机
	 */
	private void getAvataFromCamera() {
		File f = new File(CacheUtils.getCacheDirectory(personal_data_context, true, "icon")
				+ dateTime);
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Uri uri = Uri.fromFile(f);
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(camera, 1);
	}
	
	
	private void setListener() {
		//点击返回按钮，返回设置中心
		return_settings_center.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			PersonalData.this.finish();
			}
		});
		head_change_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			showAlbumDialog();
			}
		});
		//点击修改密码：密码修改在此处保存到云端
		pwd_change_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRepassword();
			}
			private void showRepassword() {
			final String email = currentUser.getEmail();
			BmobUser.resetPasswordByEmail(personal_data_context, email, new ResetPasswordByEmailListener() {
			@Override
			public void onSuccess() {
			if (currentUser.getEmailVerified() == null || (!currentUser.getEmailVerified())) {//没有邮箱验证，先进行验证
			BmobUser.requestEmailVerify(personal_data_context, email, new EmailVerifyListener() {
			@Override
			public void onSuccess() {
			Toast.makeText(personal_data_context,"修改密码，请先到"+email+"完成邮箱验证",Toast.LENGTH_SHORT).show();
			}	
			@Override
			public void onFailure(int arg0, String arg1) {
			}
			});
			} else {
			Toast.makeText(personal_data_context,"重置密码请求成功，请到" + email + "邮箱进行密码重置操作",Toast.LENGTH_SHORT).show();
			}
			}
			@Override
			public void onFailure(int arg0, String arg1) {
			Toast.makeText(personal_data_context,"重置密码请求失败",Toast.LENGTH_SHORT).show();
			}
		});
	      }
		});
	}
	/**
	 * 监听选择图片返回结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
			switch (requestCode) {
			//相机拍照完后的监听
			case 1:
				String files = CacheUtils.getCacheDirectory(personal_data_context, true,"icon") + dateTime;
				File file = new File(files);
				if (file.exists() && file.length() > 0) {
					Uri uri = Uri.fromFile(file);
					//裁剪图片
					startPhotoZoom(uri);
				}
				break;
			case 2:
				//图库选择完的监听
				if (data == null) {
					return;
				}
				//裁剪图片
				startPhotoZoom(data.getData());
				break;
			case 3:
				//裁剪完毕的监听
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap bitmap = extras.getParcelable("data");
						iconUrl = saveToSdCard(bitmap);
						head.setImageBitmap(bitmap);
						//上传头像并修改数据
						updateIcon(iconUrl);
					}
				}
				break;
			}
		}
	}
	/**
	 * 调用图库裁剪图片
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 120);
		intent.putExtra("outputY", 120);
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		// intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	/**
	 * 保存选好的图片到SD卡
	 */
	public String saveToSdCard(Bitmap bitmap) {
		String files = CacheUtils.getCacheDirectory(personal_data_context, true, "icon")+ dateTime + "_12.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
	/**
	 * 上传头像并修改数据
	 */
	private void updateIcon(String avataPath) {
		final _User currentUser = BmobUser.getCurrentUser(personal_data_context,_User.class);
		//删除旧图片
		BmobFile oldFile = currentUser.getHead();
		if(oldFile!=null){
			oldFile.delete(personal_data_context, new DeleteListener() {
				@Override
				public void onSuccess() {
					
				}
				@Override
				public void onFailure(int arg0, String arg1) {
					
				}
			});
		}
		if (avataPath != null) {
			final BmobFile file = new BmobFile(new File(avataPath));
			file.upload(personal_data_context, new UploadFileListener() {
				@Override
				public void onSuccess() {
					currentUser.setHead(file);
					currentUser.update(personal_data_context, new UpdateListener() {
						@Override
						public void onSuccess() {
							ToastFactory.showToast(personal_data_context, "修改成功");
						}
						@Override
						public void onFailure(int arg0, String arg1) {
							ToastFactory.showToast(personal_data_context, "修改失败");
						}
					});
				}
				@Override
				public void onProgress(Integer arg0) {
				}
				@Override
				public void onFailure(int arg0, String arg1) {
					ToastFactory.showToast(personal_data_context, "修改失败");
				}
			});
		}
	}
}

