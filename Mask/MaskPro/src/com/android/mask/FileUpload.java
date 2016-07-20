package com.android.mask;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

import com.bean.Resource;
import com.bean._User;
import com.util.BitmapUtil;
import com.util.ToastFactory;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileUpload extends Activity {

	private final String VIDEO_TYPE = "video/*";
	private final int VIDEO_CODE = 0;// 可以自己定义

	private RelativeLayout findVideo;
	private RelativeLayout video_info;//显示上传的视频信息
	private RelativeLayout blank_area;//点击隐藏上传标签
	private TextView upload;// 上传按钮
	private RelativeLayout return_to_mask;// 返回按钮
	private TextView title;//显示top_bar标题
	private RelativeLayout show_label_layout;// 显示标签/分类的视图
	private GridView label_gridview;
	private GridView clissify_gridview;
	private Button select_label_btn;// 确定标签的按钮
	private Context file_upload_context;
	private _User userInfo;// 用户信息
	String videoPath;// 视频的路径
	String videoPicPath;//视频的缩略图存储的路径
	// 显示视频信息的控件
	private ImageView video_img;
	private TextView video_name;
	private ProgressBar progressBarShow;
	private TextView progress_tip_tv;//提示上传进度
	private BmobFile resPic;//存储视频的缩略图
	private List<String> label_selected_list = new ArrayList<String>();
	private List<String> clissify_selected_list = new ArrayList<String>();
	
	/**
	 * 做这一步的原因是Bmob文件上传是异步上传，上传视频的同时也在执行后面的代码，这时上传图片是对数据更新，但是还不能获取到要更新的
	 * 数据的数据id，造成无法上传文件，所以，如果新生成一个bean对象需要存储，并且要上传多个文件时，需要分开上传
	 */
	private BmobFile picfile;//暂存图片上传成功之后返回的文件，存储到资源文件里面
	private String picUrl;//暂存图片上传成功之后返回的图片的地址

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_upload_layout);
		init();
		setListener();
	}

	private void init() {
		findVideo = (RelativeLayout) findViewById(R.id.find_Video);
		title = (TextView) findViewById(R.id.top_bar_title);
		title.setText("我的上传");
		upload = (TextView) findViewById(R.id.save_personal_data_update);
		upload.setText("上传");
		upload.setTextColor(Color.BLACK);
		return_to_mask = (RelativeLayout) findViewById(R.id.return_layout);
		// play = (VideoView) findViewById(R.id.videoView_play);
		file_upload_context = this;
		if (BmobUser.getCurrentUser(this,_User.class)!=null){
			userInfo = BmobUser.getCurrentUser(this,_User.class);
		}
		show_label_layout = (RelativeLayout) findViewById(R.id.want_to_upload);
		label_gridview = (GridView) findViewById(R.id.label_gridview);
		clissify_gridview = (GridView) findViewById(R.id.clissify_gridview);
		select_label_btn = (Button) findViewById(R.id.select_label_btn);
		
		//显示视频信息的控件
		video_info = (RelativeLayout) findViewById(R.id.the_video_info);
		video_img = (ImageView) findViewById(R.id.video_img);
		Log.e("------", "msg:"+video_img.getWidth()+"---"+
				video_img.getHeight());
		video_name = (TextView) findViewById(R.id.video_name);
		progressBarShow = (ProgressBar) findViewById(R.id.upload_progressBar);
		progress_tip_tv = (TextView) findViewById(R.id.progress_tip_textview);

		blank_area = (RelativeLayout) findViewById(R.id.blank_area);
		blank_area.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				show_label_layout.setVisibility(View.GONE);
			}
		});
	}

	private void setListener() {
		findVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getVideo();
			}

			/**
			 * 打开视频，选择要上传的视频
			 */
			private void getVideo() {
				// 使用Intent调用系统提供的视频功能，使用startActivityForResult
				// 是为了获取用户选择的视频
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType(VIDEO_TYPE);
				startActivityForResult(getAlbum, VIDEO_CODE);
			}
		});
		// 点击上传，跳出选择标签，当选择了标签和分类，确定按钮变亮之后才开始上传，真正上传文件是在"确定"按钮之后
		upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (videoPath == null){//还没有选择视频
					ToastFactory.showToast(file_upload_context, "请先选择视频");
				}
					
				if (show_label_layout.getVisibility() == View.VISIBLE){
					show_label_layout.setVisibility(View.GONE);
				} else if (show_label_layout.getVisibility() == View.GONE){
					show_label_layout.setVisibility(View.VISIBLE);
				}
				// 初始化标签/分类栏的页面显示，并绑定监听事件[每次点击上传都要重新初始化这个页面]
				getLabelView();
			}

		});

		select_label_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				show_label_layout.setVisibility(View.GONE);
				progressBarShow.setProgress(1);
				progress_tip_tv.setText("正在上传..."+1+"%");
				progressBarShow.setVisibility(View.VISIBLE);
				progress_tip_tv.setVisibility(View.VISIBLE);
				Log.e("------", "-------------------------------可以上传了");

				final Resource resource = new Resource();
				if (videoPicPath != null) {//更新资源信息，上传视频的缩略图
					BmobProFile.getInstance(file_upload_context).upload(videoPicPath, new UploadListener() {
						@Override
						public void onSuccess(String fileName,String url, BmobFile file) {
							picfile = file;
							picUrl = file.getUrl();
							Log.e("---meme---", "上传缩略图成功");
						}
						@Override
						public void onError(int arg0, String arg1) {
							Log.e("---meme-upload--", "上传视频缩略图失败");
						}
						@Override
						public void onProgress(int arg0) {
							Log.i("---meme---", "正在上传");
						}
					});
				}
				Log.e("----", "上传视频："+(videoPath == null));
				if (videoPath != null) {
					// 上传
					BmobProFile.getInstance(file_upload_context).upload(videoPath, new UploadListener() {
						@Override
						public void onSuccess(String fileName,String url, BmobFile file) {
							// fileName
							// ：文件名（带后缀），这个文件名是唯一的，开发者需要记录下该文件名，方便后续下载或者进行缩略图的处理
							// url ：文件地址
							// file
							// :BmobFile文件类型，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
							// 生成一条资源记录并添加到数据库
							String classify = clissify_selected_list.toString();
							classify = classify.substring(1, classify.length()-1);
							String label = label_selected_list.toString();
							label = label.substring(1, label.length()-1);
							resource.setClissify(classify);
							resource.setLabels(label);
							resource.setResName(getFileName(videoPath));
							resource.setResType("视频");
							resource.setResContent(file);
							resource.setResUrl(file.getUrl());
							resource.setPicture(picfile);
							resource.setPictureUrl(picUrl);
							if (userInfo != null){
								resource.setUser(userInfo);
							}
							resource.save(file_upload_context,new SaveListener() {
								@Override
								public void onSuccess() {
									Toast.makeText(file_upload_context,"上传文件成功",Toast.LENGTH_LONG).show();
									progressBarShow.setVisibility(View.GONE);
									progress_tip_tv.setVisibility(View.GONE);
								}
								@Override
								public void onFailure(int arg0,String arg1) {
									Log.e("---", "------"+arg0+"---"+arg1);
									Toast.makeText(file_upload_context,"上传文件失败"+arg0+"---"+arg1,
												Toast.LENGTH_LONG).show();
								}
							});
						}
						@Override
						public void onProgress(int progress) {
							progressBarShow.setProgress(progress);
							progress_tip_tv.setText("正在上传..."+progress+"%");
							Log.e("bmob", "onProgress :" + progress);
						}

						@Override
						public void onError(int statuscode,	String errormsg) {
							Log.i("bmob", "文件上传失败：" + errormsg);
						}
				});

				} 
			}
		});

		return_to_mask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.putExtra("result", " ");
		        FileUpload.this.setResult(RESULT_OK, intent);
		        FileUpload.this.finish();
			}
		});
	}
	/**
	 * 保存数据		
	 * @param obj
	 */
	private void insertObject(final Resource obj){
		String classify = clissify_selected_list.toString();
		classify = classify.substring(1, classify.length()-1);
		String label = label_selected_list.toString();
		label = label.substring(1, label.length()-1);
		obj.setClissify(classify);
		obj.setLabels(label);
		obj.setResType("视频");
		obj.setPicture(picfile);
		if (userInfo != null){
			obj.setUser(userInfo);
		}
		
		obj.save(file_upload_context, new SaveListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(file_upload_context,"上传文件成功",Toast.LENGTH_LONG).show();
				progressBarShow.setVisibility(View.GONE);
				progress_tip_tv.setVisibility(View.GONE);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(file_upload_context,"上传文件失败",Toast.LENGTH_LONG).show();
			    Log.e("-------保存数据的时候：","-->创建数据失败：" + arg0+",msg = "+arg1);
			}
		});
	}		
	/**
	 * 根据文件路径获取文件名
	 * 
	 * @param videoPath
	 * @return
	 */
	private String getFileName(String videoPath) {
		int start = videoPath.lastIndexOf("/");
		int end = videoPath.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return videoPath.substring(start + 1, end);
		} else {
			return null;
		}
	}

	// 初始化适配器，为GridView添加数据，并设置item的点击事件
	private void getLabelView() {
		//每次重新初始化gridView都要重新设置按钮不可点，并且清空上次选中的标签
		label_selected_list.clear();
		clissify_selected_list.clear();
		select_label_btn.setClickable(false);
		select_label_btn.setBackgroundResource(R.drawable.button_color_n);
		
		final String[] label_data = { "甜美", "烟熏", "清新", "魅惑", "个性", "日常",
				"小技巧", "自然", "女神", "校园","淡妆" };
		final String[] clissify_data = { "整体", "眼妆", "眉妆", "唇妆", "修容", "卸妆" };
		// 初始化适配器
		final MyCheckAdapter label_gridiew_adapter = new MyCheckAdapter(
				file_upload_context, label_data);
		final MyCheckAdapter clissify_gridiew_adapter = new MyCheckAdapter(
				file_upload_context, clissify_data);
		// 将适配器和GridView和适配器绑定
		label_gridview.setAdapter(label_gridiew_adapter);
		clissify_gridview.setAdapter(clissify_gridiew_adapter);
		// 设置item的点击事件
		label_gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyViewHolder holder = (MyViewHolder) view.getTag();
				holder.cb.toggle();
				label_gridiew_adapter.isSelected.put(position,
						holder.cb.isChecked());
				if (holder.cb.isChecked() == true) {
					label_selected_list.add(label_data[position]);
				} else {
					label_selected_list.remove(label_data[position]);
				}
				Log.e("----", "----label_selected_list-----"
						+ label_selected_list.toString());
				if ((label_selected_list.size() > 0)
						&& (clissify_selected_list.size() > 0)) {
					select_label_btn.setClickable(true);
					select_label_btn
							.setBackgroundResource(R.drawable.btn_color);
				}
			}
		});
		clissify_gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyViewHolder holder = (MyViewHolder) view.getTag();
				holder.cb.toggle();
				clissify_gridiew_adapter.isSelected.put(position,
						holder.cb.isChecked());
				if (holder.cb.isChecked() == true) {
					clissify_selected_list.add(clissify_data[position]);
				} else {
					clissify_selected_list.remove(clissify_data[position]);
				}
				Log.e("----", "-----clissify_selected_list----"
						+ clissify_selected_list.toString());
				if ((label_selected_list.size() > 0)
						&& (clissify_selected_list.size() > 0)) {
					select_label_btn.setClickable(true);
					select_label_btn
							.setBackgroundResource(R.drawable.btn_color);
				}
			}
		});

	}

	/**
	 * 接收返回的视频信息
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {// RESULT_OK是系统自定义的常量
			Log.e("TAG->onresult", "ActivityResult resultCode error");
			return;
		}

		// 外界程序访问ContentProvider锁提供的数据，可通过ContentResolver接口
		ContentResolver resolver = getContentResolver();
		// 此处用于判断接收的Activity是不是你想要的那个

		if (requestCode == VIDEO_CODE) {
			Uri originalUri = data.getData();// 获取视频的Uri
			// 获取地址
			String[] proj = { MediaStore.Video.Media.DATA };

			// android多媒体数据库的封装接口
			Cursor cursor = managedQuery(originalUri, proj, null, null, null);
			// 获得用户选择的图片的索引值
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			// 将光标移至开头，不然很容易引起越界（其实我是不懂的）
			cursor.moveToFirst();

			// 最后根据索引值获取图片路径

			videoPath = cursor.getString(column_index);
			// 根据视频路径获取视频缩略图，并显示
			Bitmap videobm = getVideoThumbnail(videoPath, 512, 384, MediaStore.Video.Thumbnails.MINI_KIND);
			Log.e("------", "msg:"+(video_img == null)+video_img.getWidth()+"---"+
					video_img.getHeight());
			//把缩略图压缩到imageview大小
			video_img.setImageBitmap(Bitmap.createScaledBitmap(videobm, video_img.getWidth(),
					video_img.getHeight(), false));
			video_name.setText(videoPath.substring(videoPath.lastIndexOf("/")+1));
			
			videoPicPath = BitmapUtil.saveBitmap2file(videobm, getFileName(videoPath));
			//选择视频之后显示视频的信息
			video_info.setVisibility(View.VISIBLE);
			Log.e("/////////", "picPath:"+videoPicPath);
		}
	}
	
	private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){
		  Bitmap bitmap = null;
		  bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		  return bitmap;
	}
}


