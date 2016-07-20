package com.android.recommend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.adapter.EmoViewPagerAdapter;
import com.adapter.EmoteAdapter;
import com.android.mask.R;
import com.bean.Comment;
import com.bean.FaceText;
import com.bean.Resource;
import com.bean._User;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.util.BitmapUtil;
import com.util.CacheUtils;
import com.util.FaceTextUtils;
import com.util.ToastFactory;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 发表高级评论,所需参数: QiangYu - "qiangYu" 评论绑定的帖子 Comment - "parentComment" 评论的父评论
 * 
 * @author dulang
 */
public class aCoursePublishCommentActivity extends Activity {
	private static final int REQUEST_CODE_ALBUM = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	private EditText editContent;
	private ImageView addImage;
	private CheckBox niminCheckBox;
	private ViewPager pager_emo;
	private ImageButton openEm;
	private String dateTime;
	private Context mContext;
	private Comment parentComment;
	private Resource qiangYu;
	private List<FaceText> emos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_publish);

		initView(); // 初始化界面
		TextView infoText = (TextView) findViewById(R.id.publish_info);
		if (parentComment != null) {
			infoText.setText(" 回复:" + parentComment.getUser().getUserNick());
		} else {
			infoText.setVisibility(View.GONE);
		}
	}

	/*
	 * 初始化组件
	 */
	private void initView() {
		mContext = this;
		editContent = (EditText) findViewById(R.id.publish_edit);
		addImage = (ImageView) findViewById(R.id.publish_addimage);
		niminCheckBox = (CheckBox) findViewById(R.id.activity_pub_nimin);
		// 得到发表评论所需要的条件
		parentComment = (Comment) getIntent().getSerializableExtra(
				"parentComment");
		qiangYu = (Resource) getIntent().getSerializableExtra("qiangYu");
		openEm = (ImageButton) findViewById(R.id.pub_open_em);
		pager_emo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; ++i) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
		// 添加表情按钮的点击事件
		openEm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (pager_emo.getVisibility() == View.GONE) {
					pager_emo.setVisibility(View.VISIBLE);
				} else {
					pager_emo.setVisibility(View.GONE);
				}
			}
		});
		// 设置选择图片按钮的点击事件
		addImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDilog();
			}
		});
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
			String commitContent = editContent.getText().toString().trim();
			if (TextUtils.isEmpty(commitContent)) {
				ToastFactory.showToast(mContext, "说点什么吧");
			} else if (targeturl == null) {
				publishWithoutFigure(commitContent, null);
			} else {
				publish(commitContent);
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
			public void onFailure(int arg0, String arg1) {
				ToastFactory.showToast(mContext, "上传图片失败");
				finish();
			}
		});
	}

	private void publishWithoutFigure(final String commitContent,
			final BmobFile figureFile) {
		final Comment comment = new Comment();
		final _User user = BmobUser.getCurrentUser(mContext, _User.class);
		comment.setUser(user);
		comment.setCommentContent(commitContent);
		// 判断是否评论的是自己帖子
		if (!user.getObjectId().equals(qiangYu.getUser().getObjectId())) {
			// 判断父评论是否为空
			if (parentComment != null) {
				comment.setParent(parentComment);
				comment.setToUser(qiangYu.getUser().getObjectId() + ";"
						+ parentComment.getUser().getObjectId());
			} else {
				comment.setToUser(qiangYu.getUser().getObjectId());
			}
		} else {
			// 判断父评论是否为空
			if (parentComment != null) {
				comment.setParent(parentComment);
				comment.setToUser(parentComment.getUser().getObjectId());
			}
		}
		if (figureFile != null) {
			comment.setContentfigureurl(figureFile);
		}
		comment.setAnonymous(niminCheckBox.isChecked());
		comment.setToNoteResource(qiangYu);
		comment.setRead(false);
		comment.save(mContext, new SaveListener() {
			@Override
			public void onSuccess() {
				// 发送推送消息给被评论的用户,判断父评论是否为空
				// UserDao dao = new UserDao(mContext);
				// if(parentComment!=null){
				// dao.pushMsg(qiangYu.getAuthor().getInstallId(),
				// user.getNick()+" 在你的帖子中回复了"+parentComment.getUser().getNick());
				// dao.pushMsg(parentComment.getUser().getInstallId(),
				// user.getNick()+" 回复了你的评论");
				// }else{
				// dao.pushMsg(qiangYu.getAuthor().getInstallId(),
				// user.getNick()+" 评论了你的帖子");
				// }
				// 将该评论与帖子绑定到一起
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				qiangYu.setCommentNumber(qiangYu.getCommentNumber() + 1);
				qiangYu.setComments(relation);
				qiangYu.update(mContext, new UpdateListener() {
					@Override
					public void onSuccess() {
						ToastFactory.showToast(mContext, "评论成功");
						finish();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ToastFactory.showToast(mContext, "评论失败：" + arg1);
						finish();
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ToastFactory.showToast(mContext, "评论失败：" + arg1);
				finish();
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
