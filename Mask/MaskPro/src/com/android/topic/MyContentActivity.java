package com.android.topic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.adapter.AIContentAdapter;
import com.dao.QiangYuDao;
import com.android.mask.Constant;

import com.android.mask.R;
import com.android.settings.SettingsCenterActivity;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.bean.Topic;
import com.bean._User;
import com.util.ToastFactory;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.myview.MyConfirmDialog;

/**
 * 展示用户的帖子或者收藏,需要两个参数：
 * User - "user" 当前的用户
 * boolean - "isFav" 是否是收藏
 * @author dulang
 */
public class MyContentActivity extends Activity{
	private PullToRefreshListView refreshList; //列表
	private ListView mainList;
	private ArrayList<Topic> mListItems; //帖子列表
	private AIContentAdapter mAdapter; //列表适配器
	private _User mUser;
	private boolean isFav;
	private boolean haveData = true,isRefresh = false; //是否还有数据
	private int pageNum = 0; //当前加载到第几页
	private Context mContext;
	
	private LinearLayout bg_logo_layout;
	private TextView tips;//提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.activity_mycontent);
		
        initView(); //初始化组件
		//设置列表事件监听处理
		setListInfo();
		//设置进入界面刷新
		refreshList.setRefreshing(true);
	}
	/**
	 * 初始化组件
	 */
	private void initView(){
		mContext = this;
        refreshList = (PullToRefreshListView) findViewById(R.id.main_list);
        mainList = refreshList.getRefreshableView();
        //初始化数据集合,为列表设置适配器
        mListItems = new ArrayList<Topic>();
		mAdapter = new AIContentAdapter(this, mListItems);
		mainList.setAdapter(mAdapter);
		//得到查询的条件
	//	mUser = (User) getIntent().getSerializableExtra("user");
		mUser = BmobUser.getCurrentUser(mContext,_User.class);
		
		bg_logo_layout = (LinearLayout) findViewById(R.id.bg_logo_layout);
		tips = (TextView) findViewById(R.id.text_tips);//没有收藏时的文字提示
	}
	/**
	 * 查询当前用户发表的帖子或者收藏的帖子
	 */
	private void queryContent(){
		if(!haveData){
			return;
		}
		BmobQuery<Topic> query = new BmobQuery<Topic>();
		query.order("-createdAt");
//		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		query.setLimit(Constant.NUMBERS_PER_PAGE);
		query.setSkip(Constant.NUMBERS_PER_PAGE*(pageNum++));
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		query.addWhereLessThan("createdAt", date);
		//判断是否是收藏设置查询条件
		query.addWhereEqualTo("author", mUser);
		query.include("author");
		query.findObjects(this, new FindListener<Topic>() {
			@Override
			public void onSuccess(List<Topic> list) {
				if(list.size()!=0&&list.get(list.size()-1)!=null){
					if(isRefresh){
						mListItems.clear();
					}
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();
					//没有更过数据了
					if(list.size()<Constant.NUMBERS_PER_PAGE){
						haveData = false;
					}
				}else{
					ToastFactory.showToast(mContext, "还没有数据");
					haveData = false;
					bg_logo_layout.setVisibility(View.VISIBLE);
				}
				refreshList.onRefreshComplete();
			}
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mContext, "获取数据失败"+arg1);
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
				queryContent();
			}
			//上拉加载更多
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(haveData){
					isRefresh = false;
					queryContent();
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
		mainList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				_User currenUser = BmobUser.getCurrentUser(mContext, _User.class);
				if(!currenUser.getObjectId().equals(mUser.getObjectId())){
					Intent intent = new Intent();
					intent.setClass(mContext, CommentActivity.class);
					intent.putExtra("data", mListItems.get(position-1));
					startActivity(intent);
					return;
				}
				if(isFav){
					showDialog(mListItems.get(position-1), new String[]{"查看评论","删除帖子","取消收藏"});
				}else{
					showDialog(mListItems.get(position-1), new String[]{"查看评论","删除帖子"});
				}
			}
		});
	}
	/**
	 * 显示列表点击的对话框
	 */
	private void showDialog(final Topic data,final String[] stringItems){
        final ActionSheetDialog menuDialog = new ActionSheetDialog(mContext, stringItems, null);
        menuDialog.setOnOperItemClickL(new OnOperItemClickL() {
			@Override
			public void onOperItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final QiangYuDao dao = new QiangYuDao();
				switch (position) {
				case 0:
					Intent intent = new Intent();
					intent.setClass(mContext, CommentActivity.class);
					intent.putExtra("data", data);
					startActivity(intent);
					break;
				case 1:
					if(!data.getAuthor().getObjectId().equals(mUser.getObjectId())){
						ToastFactory.showToast(mContext, "你不能删除别人的帖子");
						return;
					}
					//删除帖子
					final MyConfirmDialog cDialog = new MyConfirmDialog(mContext);
					cDialog.setTitle("删除帖子");
					cDialog.setContent("确定删除吗？");
					cDialog.setConfirmButtonListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cDialog.dismiss();
							dao.deleteQiangYu(mContext, data, mAdapter);
						}
					});
					cDialog.show();
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
	菜单点击事件
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				MyContentActivity.this.finish();
	   		    break;
		}
		return super.onOptionsItemSelected(item);
	}
}
