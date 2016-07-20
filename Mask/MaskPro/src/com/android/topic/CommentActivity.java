package com.android.topic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.adapter.CommentAdapter;
import com.android.mask.Constant;
import com.android.mask.R;
import com.android.settings.LoginActivity;
import com.bean.Comment;
import com.bean.Topic;
import com.bean._User;
import com.dao.CommentDao;
import com.dao.QiangYuDao;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.util.ToastFactory;

/**
 * 展示评论列表,需要一个参数:
 * QiangYu - "data" 要查询的帖子
 * @author dulang
 */
public class CommentActivity extends Activity implements OnClickListener {
	private Context mContext;
	private PullToRefreshListView refreshList; //pullListView
	private ListView commentList;	//评论列表
	private EditText commentContent;	//评论框
	private Button commentCommit;	//评论提交按钮
	private ViewGroup headGroup;
	private TextView userName;	//楼主用户名
	private TextView timeText,contentText;//帖子内容
	private ImageView contentImage,userLogo; //帖子图片,楼主头像
	private TextView lookText,commentText;	//
	private ImageView shareBtn;	//收藏
	private Topic qiangYu;	//帖子
	private ImageView addBtn; //发表高级评论的按钮
	private CommentAdapter mAdapter;
	private List<Comment> comments = new ArrayList<Comment>();
	private int pageNum = 0; //当前加载到第几页
	private boolean haveData=true,isRefresh=false;//是否还有数据
	private _User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_comment);
		
		//得到帖子内容
		qiangYu = (Topic) getIntent().getSerializableExtra("data");
		//初始化组件
		initView();
		//添加头部
		commentList.addHeaderView(headGroup);
		//设置适配器
		commentList.setAdapter(mAdapter);
		//设置pullList属性及事件
		setListInfo();
		//设置进入界面刷新,加载评论
		refreshList.setRefreshing();
		//设置该帖的浏览数量增加1个
		QiangYuDao dao = new QiangYuDao();
		dao.incrementLook(mContext, qiangYu);
	}
	/**
	 * 初始化组件
	 */
	protected void initView() {
		mContext = this;
		currentUser = BmobUser.getCurrentUser(mContext, _User.class);
		//得到组件
		refreshList = (PullToRefreshListView) findViewById(R.id.comment_list);
		commentList = refreshList.getRefreshableView();
		commentContent = (EditText) findViewById(R.id.comment_content);
		commentCommit = (Button) findViewById(R.id.comment_commit);
		headGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.item_list_content, null);
		userLogo = (ImageView) headGroup.findViewById(R.id.user_logo);
		userName = (TextView) headGroup.findViewById(R.id.user_name);
		timeText = (TextView) headGroup.findViewById(R.id.school);
		contentText = (TextView) headGroup.findViewById(R.id.content_text);
		contentImage = (ImageView) headGroup.findViewById(R.id.content_image);
		shareBtn = (ImageView) headGroup.findViewById(R.id.item_action_fav);
		lookText = (TextView) headGroup.findViewById(R.id.item_action_love);
		commentText = (TextView) headGroup.findViewById(R.id.item_action_comment);
		addBtn = (ImageView) findViewById(R.id.comment_add);
		//加载帖子内容
		initMoodView(qiangYu);
		//为组件设置监听事件
		commentCommit.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		//为列表设置适配
		mAdapter = new CommentAdapter(CommentActivity.this, comments);
	}
	/**
	 * 加载帖子内容
	 */
	private void initMoodView(Topic mood2) {
		if (mood2 == null) {
			return;
		}
		//判断是不是匿名
		if(mood2.isAnonymous()){
			userName.setText("匿名");
		}else{
			_User temUser = mood2.getAuthor();
			userName.setText(temUser.getUserNick());
			BmobFile avatar = temUser.getHead();
			if (null != avatar) {
				avatar.loadImage(mContext, userLogo);
			}
			userLogo.setOnClickListener(this);
		}
		timeText.setText(mood2.getCreatedAt());
		lookText.setText((mood2.getLookNumber()+1) + "");
		commentText.setText(mood2.getCommentNumber()+"");
		contentText.setText(mood2.getContent());
		if (null == mood2.getContentfigureurl()) {
			contentImage.setVisibility(View.GONE);
		} else {
			contentImage.setVisibility(View.VISIBLE);
			mood2.getContentfigureurl().loadImage(mContext, contentImage);
			LayoutParams params  = contentImage.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			contentImage.setLayoutParams(params);
//			//帖子图片的点击事件
//			contentImage.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					Intent intent = new Intent();
//					intent.setClass(mContext, LookImageActivity.class);
//					intent.putExtra("url", url);
//					mContext.startActivity(intent);
//				}
//			});
		}
	}
	/**
	 * 加载评论
	 */
	private void fetchComment() {
		if(!haveData){
			return;
		}
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(qiangYu));
		query.include("user.rank,parent.user");
		query.order("createdAt");
		query.setLimit(Constant.NUMBERS_PER_PAGE);
		query.setSkip(Constant.NUMBERS_PER_PAGE*(pageNum++));
		query.findObjects(this, new FindListener<Comment>() {
			@Override
			public void onSuccess(List<Comment> data) {
				if(data.size()!=0&&data.get(data.size()-1)!=null){
					if(isRefresh){
						comments.clear();
					}
					comments.addAll(data);
					mAdapter.notifyDataSetChanged();
					//没有更过数据了
					if(data.size()<Constant.NUMBERS_PER_PAGE){
						haveData = false;
					}
				}else{
					ToastFactory.showToast(mContext, "还没有评论,快发表第一个吧");
					haveData = false;
				}
				refreshList.onRefreshComplete();
			}
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mContext, "加载评论失败："+arg1);
			}
		});
	}
	/**
	 * 设置列表属性及事件监听
	 */
	private void setListInfo(){
		refreshList.setMode(Mode.BOTH);
		refreshList.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		refreshList.getLoadingLayoutProxy(true, false).setReleaseLabel("松开刷新");
		refreshList.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		refreshList.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		refreshList.getLoadingLayoutProxy(false, true).setReleaseLabel("松开加载更多");
		refreshList.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
		refreshList.getLoadingLayoutProxy(true, true).setLoadingDrawable(getResources().getDrawable(R.drawable.refresh));
		//下拉刷新
		refreshList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageNum = 0;
				haveData = true;
				isRefresh = true;
				fetchComment();
			}
			//上拉加载更多
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(haveData){
					isRefresh = false;
					fetchComment();
				}else{
					refreshList.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	ToastFactory.showToast(mContext, "已加载完所有数据");
			            	refreshList.onRefreshComplete();
			            }
			        }, 500);
				}
			}
		});
		//列表点击事件
		commentList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){
					return;
				}
				if(position==1){
					showDialog(null,new String[]{"复制内容"});
					return;
				}
				currentUser = BmobUser.getCurrentUser(mContext,_User.class);
				if(currentUser== null)
				{
				Toast.makeText(getApplicationContext(), "请先登录",
			    Toast.LENGTH_SHORT).show();
				}
				else
				{
				String userId = comments.get(position - 2).getUser().getObjectId();
				if(userId.equals(currentUser.getObjectId())){
					showDialog(comments.get(position - 2),new String[]{"复制内容","回复","删除评论"});
				}else{
					showDialog(comments.get(position - 2),new String[]{"复制内容","回复"});
				}		
				}	
			}
		});
	}
	/**
	 * 用户头像点击事件
	 */
	private void onClickUserLogo() {
//		Intent intent = new Intent();
//		intent.setClass(mContext, PersonalActivity.class);
//		intent.putExtra("user", qiangYu.getAuthor());
//		mContext.startActivity(intent);
	}
	/**
	 * 评论按钮点击事件
	 */
	private void onClickCommit() {
		if (TextUtils.isEmpty(commentContent.getText())) {
			ToastFactory.showToast(mContext, "说点什么吧");
			return;
		}
		CommentDao dao = new CommentDao();
		dao.publishComment(mContext,commentContent,currentUser,qiangYu,mAdapter);
	}
	/**
	 * 显示列表点击的对话框
	 */
	private void showDialog(final Comment comment,final String[] stringItems){
        final ActionSheetDialog menuDialog = new ActionSheetDialog(mContext, stringItems, null);
        menuDialog.setOnOperItemClickL(new OnOperItemClickL() {
			@Override
			public void onOperItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
				case 0:
					if(comment==null){
//						StringUtils.setClip(mContext, qiangYu.getContent());
					}else{
//						StringUtils.setClip(mContext, comment.getCommentContent());
					}
					break;
				case 1:
					if(stringItems.length>2){
						ToastFactory.showToast(mContext, "不能回复自己");
					}else{
						//跳转到高级回复界面
						Intent intent = new Intent();
						intent.setClass(mContext, PublishCommentActivity.class);
						intent.putExtra("qiangYu", qiangYu);
						intent.putExtra("parentComment", comment);
						mContext.startActivity(intent);
					}
					break;
				case 2:
					CommentDao dao = new CommentDao();
					dao.deleteComment(mContext, comment, qiangYu, mAdapter);
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
	 * 各个组件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_logo:
			onClickUserLogo(); break;
		case R.id.comment_commit:
			if(BmobUser.getCurrentUser(mContext)==null){
				Intent intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				mContext.startActivity(intent);
			}else{
				onClickCommit();
			}
			 break;
		case R.id.comment_add:
			haveData = true;
			//跳转到高级回复界面
			Intent intent = new Intent();
			if(BmobUser.getCurrentUser(mContext)==null){
				intent.setClass(mContext, LoginActivity.class);
			}else{
				intent.setClass(mContext, PublishCommentActivity.class);
				intent.putExtra("qiangYu", qiangYu);
			}
			mContext.startActivity(intent);
			break;
		default: break;
		}
	}
	/**
	菜单点击事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
	   		    break;
		}
		return super.onOptionsItemSelected(item);
	}
}
