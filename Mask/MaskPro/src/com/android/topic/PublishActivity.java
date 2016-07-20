package com.android.topic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.myview.MyProgressDialog;
import com.adapter.EmoViewPagerAdapter;
import com.adapter.EmoteAdapter;
import com.android.mask.Constant;
import com.android.mask.R;
import com.bean.FaceText;
import com.bean.Topic;
import com.bean._User;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.myview.EmoticonsEditText;
import com.util.BitmapUtil;
import com.util.CacheUtils;
import com.util.FaceTextUtils;
import com.util.ToastFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 发表帖子,需要两个参数：
 *  String - "type" 帖子类型
 * @author dulang
 */
public class PublishActivity extends Activity {
	private static final int REQUEST_CODE_ALBUM = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	private EmoticonsEditText editContent;
	private ImageView addImage;
	private CheckBox niminCheckBox;
	private ViewPager pager_emo;
	private ImageButton openEm;
	private MyProgressDialog pDialog;
	private String type2, dateTime;
	private _User user;
	private Context mContext;
	private List<FaceText> emos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);  
		setContentView(R.layout.activity_publish);

		initView(); // 初始化界面
		TextView infoText = (TextView) findViewById(R.id.publish_info);
	}

	/*
	 * 初始化组件
	 */
	private void initView() {
		mContext = this;
		editContent = (EmoticonsEditText) findViewById(R.id.publish_edit);
		addImage = (ImageView) findViewById(R.id.publish_addimage);
		niminCheckBox = (CheckBox) findViewById(R.id.activity_pub_nimin);
		openEm = (ImageButton) findViewById(R.id.pub_open_em);
		pager_emo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; ++i) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
		//添加表情按钮的点击事件
		openEm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(pager_emo.getVisibility()==View.GONE){
					pager_emo.setVisibility(View.VISIBLE);
				}else{
					pager_emo.setVisibility(View.GONE);
				}
			}
		});
		//添加图片按钮的点击事件
		addImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 设置菜单点击的对话框
				showDilog();
			}
		});
		pDialog = new MyProgressDialog(mContext);
		pDialog.setMsg("正在发表..");
		user = BmobUser.getCurrentUser(mContext, _User.class);
	}

	/**
	 * 显示选择图片方式的对话框
	 */
	private void showDilog() {
		String[] stringItems = { "图库选择", "拍照上传" };
		final ActionSheetDialog menuDialog = new ActionSheetDialog(mContext,
				stringItems, null);
		menuDialog.setOnOperItemClickL(new OnOperItemClickL() {
			@Override
			public void onOperItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Date date1 = new Date(System.currentTimeMillis());
					dateTime = date1.getTime() + "";
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setDataAndType(
							MediaStore.Images.Media.INTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, REQUEST_CODE_ALBUM);
					break;
				case 1:
					Date date = new Date(System.currentTimeMillis());
					dateTime = date.getTime() + "";
					File f = new File(CacheUtils.getCacheDirectory(mContext,
							true, "pic") + dateTime);
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
					startActivityForResult(camera, REQUEST_CODE_CAMERA);
					break;
				default:
					break;
				}
				menuDialog.dismiss();
			}
		});
		menuDialog.isTitleShow(false).show();
	}

	/**
	 * 菜单及其点击事件
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.publish, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.publish_menu_pub:
			final String commitContent = editContent.getText().toString().trim();
			if (TextUtils.isEmpty(commitContent)) {
				ToastFactory.showToast(mContext, "说点什么吧");
			} else{
				if (targeturl == null) {
					pDialog.show();
					publishWithoutFigure(commitContent, null);
				} else {
					pDialog.show();
					publish(commitContent);
				}
//				final String[] stringItems = {Constant.TYPE_1,Constant.TYPE_2,Constant.TYPE_3};
//				final ActionSheetDialog typeDialog = new ActionSheetDialog(mContext,stringItems, null);
//				typeDialog.title("请选择分类");
//				//设置类型选择的点击事件
//				typeDialog.setOnOperItemClickL(new OnOperItemClickL() {
//					@Override
//					public void onOperItemClick(AdapterView<?> parent,
//							View view, int position, long id) {
//						type2 = stringItems[position];
//						typeDialog.dismiss();
//
//					}
//				});
//				typeDialog.show();
			}
			break;
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * 发表带图片
	 */
	private void publish(final String commitContent) {
		// final BmobFile figureFile = new BmobFile(QiangYu.class, new
		// File(targeturl));
		final BmobFile figureFile = new BmobFile(new File(targeturl));
		figureFile.upload(mContext, new UploadFileListener() {
			@Override
			public void onSuccess() {
				publishWithoutFigure(commitContent, figureFile);
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				pDialog.cancel();
				ToastFactory.showToast(mContext, "上传图片失败");
			}
		});
	}

	private void publishWithoutFigure(final String commitContent,
			final BmobFile figureFile) {
		final Topic qiangYu = new Topic();
		qiangYu.setAuthor(user);
		qiangYu.setContent(commitContent);
		if (figureFile != null) {
			qiangYu.setContentfigureurl(figureFile);
		}
		qiangYu.setLookNumber(0);
		qiangYu.setCommentNumber(0);
		qiangYu.setAnonymous(niminCheckBox.isChecked());
		qiangYu.setType(type2);
		qiangYu.save(mContext, new SaveListener() {
			@Override
			public void onSuccess() {
				pDialog.cancel();
				ToastFactory.showToast(mContext, "发表成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				pDialog.cancel();
				ToastFactory.showToast(mContext, "发表失败" + arg1);
			}
		});
	}

	String targeturl = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			BitmapUtil bUtil = new BitmapUtil();
			switch (requestCode) {
			case REQUEST_CODE_ALBUM:
				String fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToFirst()) {
						do {
							fileName = cursor.getString(cursor
									.getColumnIndex("_data"));
						} while (cursor.moveToNext());
					}
					Bitmap bitmap = bUtil.compressImageFromFile(fileName);
					targeturl = bUtil.saveToSdCard(bitmap, mContext, dateTime);
					addImage.setImageBitmap(bitmap);
				}
				break;
			case REQUEST_CODE_CAMERA:
				String files = CacheUtils.getCacheDirectory(mContext, true,
						"pic") + dateTime;
				File file = new File(files);
				if (file.exists()) {
					Bitmap bitmap = bUtil.compressImageFromFile(files);
					targeturl = bUtil.saveToSdCard(bitmap, mContext, dateTime);
					addImage.setImageBitmap(bitmap);
				} else {
				}
				break;
			default:
				break;
			}
		}
	}
	/**
	 * 得到表情的网格布局
	 */
	private View getGridView(final int i) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(mContext, list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (editContent != null && !TextUtils.isEmpty(key)) {
						int start = editContent.getSelectionStart();
						CharSequence content = editContent.getText().insert(
								start, key);
						editContent.setText(content);
						// 定位光标位置
						CharSequence info = editContent.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}

			}
		});
		return view;
	}
}
