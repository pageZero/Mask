package com.adapter;

import java.util.List;

import com.android.mask.R;
import com.bean.Resource;
import com.bean._User;
import com.myview.EmoticonsTextView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CourseAdapter extends BaseContentAdapter<Resource> {

	public CourseAdapter(Context context, List<Resource> list) {
		super(context, list);
	}

	/**
	 * 实现父类中得到每一项布局的抽象方法
	 */
	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_maincourse, null);
			viewHolder.userNameText = (TextView) convertView
					.findViewById(R.id.course_user_name);
			viewHolder.timeText = (TextView) convertView
					.findViewById(R.id.course_time);
			viewHolder.userLogo = (ImageView) convertView
					.findViewById(R.id.course_user_logo);
			viewHolder.shareBtn = (ImageView) convertView
					.findViewById(R.id.course_item_action_fav);
			viewHolder.contentText = (EmoticonsTextView) convertView
					.findViewById(R.id.course_content_text);
			viewHolder.contentImage = (ImageView) convertView
					.findViewById(R.id.course_content_image);
			viewHolder.lookNumberText = (TextView) convertView
					.findViewById(R.id.course_item_action_love);
			viewHolder.commentNumberText = (TextView) convertView
					.findViewById(R.id.course_item_action_comment);
			viewHolder.contentText.setMaxLines(5);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 得到当前视频资源
		final Resource entity = dataList.get(position);
		// 设置视频名字
		viewHolder.contentText.setText(entity.getResName());
		// 设置浏览数量
		if(entity.getLookNumber() != null){
			viewHolder.lookNumberText.setText(entity.getLookNumber().toString());
		}else{
			viewHolder.lookNumberText.setText("0");
		}
		// 设置评论数量
		if(entity.getLookNumber() != null){
			viewHolder.commentNumberText.setText(entity.getCommentNumber().toString());
		}else{
			viewHolder.commentNumberText.setText("0");
		}
		
		// 设置发表时间
		viewHolder.timeText.setText(entity.getUpdatedAt());
		// 判断是不是匿名
		_User user = entity.getUser();
		if (user == null ) {
			viewHolder.userNameText.setText("匿名");
			// 设置发表时间
			viewHolder.userLogo
					.setImageResource(R.drawable.user_icon_default_main);
			// 头像设置点击事件
			viewHolder.userLogo.setOnClickListener(null);
		} else {
			if(user.getUserNick() == null ){
				viewHolder.userNameText.setText("不愿留名的Ta");
			}else{
			// 设置用户名
			    viewHolder.userNameText.setText(user.getUserNick());
			}
			// 加载用户头像
			if (user.getHead() != null) {
				user.getHead().loadImage(mContext, viewHolder.userLogo);
			}
		
		}
		// 设置帖子图片
		if (null == entity.getPicture()) {
			viewHolder.contentImage.setVisibility(View.GONE);
		} else {
			viewHolder.contentImage.setVisibility(View.VISIBLE);
			entity.getPicture().loadImage(mContext,
					viewHolder.contentImage);
		}
		// 分享按钮点击事件
		viewHolder.shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				String url = "";
				if (entity.getResUrl() != null) {
					url = entity.getResUrl();
				}
				intent.putExtra(Intent.EXTRA_TEXT, entity.getResName() + "\n"
						+ url);
				mContext.startActivity(Intent.createChooser(intent, "分享帖子"));
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		public ImageView userLogo; // 头像
		public TextView userNameText; // 用户名
		public TextView timeText; // 时间
		public EmoticonsTextView contentText; // 内容
		public ImageView contentImage; // 图片
		public ImageView shareBtn; // 收藏
		public TextView lookNumberText; // 点赞
		public TextView commentNumberText; // 评论
	}

	/**
	 * 头像及用户名的点击事件
	 */
	private class MyUserOnClick implements OnClickListener {
		_User user;

		public MyUserOnClick(_User user) {
			super();
			this.user = user;
		}

		@Override
		public void onClick(View arg0) {
			// Intent intent = new Intent();
			// intent.setClass(mContext, PersonalActivity.class);
			// intent.putExtra("user", user);
			// mContext.startActivity(intent);
		}
	}
}