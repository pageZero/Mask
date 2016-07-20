package com.android.classify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

import com.adapter.AIContentAdapter;
import com.adapter.CourseAdapter;
import com.android.mask.Constant;
import com.android.mask.R;
import com.android.recommend.RecommendActivity;
import com.android.settings.LoginActivity;
import com.android.settings.SettingsCenterActivity;
import com.android.topic.CommentActivity;
import com.android.topic.PublishActivity;
import com.bean.Resource;
import com.bean._User;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.util.ToastFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EachClassifyActivity extends Activity {
	private RelativeLayout to_classifyActivity;
	private String classifyName;
	
	// 1~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	protected ArrayList<Resource> mListItems; // 帖子列表
	private CourseAdapter mAdapter; // 列表适配器
	private PullToRefreshListView refreshList; // 列表
	private ListView mainList;
	private RadioGroup headTypeGroup;
	private String type2;// 板块类型
	private int pageNum = 0; // 当前加载到第几页
	private boolean haveData = true, isRefresh = false; // 是否还有数据
	private Context mContext;
	private TextView title;// top_bar上面的文字提示
	// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// 1~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private _User userInfo;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消默认top bar
		setContentView(R.layout.class_each); // 绑定当前layout为activity_main.xml

		Intent intent=getIntent();
		classifyName = intent.getStringExtra("类名");//接受传过来的ID为“类名”的参数
		initView(); // 初始化组件
		// 设置列表事件监听处理
		setListInfo();
		// 设置进入界面刷新
		refreshList.setRefreshing(true);
	}
	private void initView() {
		mContext = this;
		
		to_classifyActivity = (RelativeLayout) findViewById(R.id.return_layout);
		to_classifyActivity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				finish();			}
		});
		
		title = (TextView) findViewById(R.id.top_bar_title);
		title.setText(classifyName +"分类列表");
		
		
		refreshList = (PullToRefreshListView) findViewById(R.id.main_list);
		mainList = refreshList.getRefreshableView();
		// 得到列表头部的类型筛选组件
		headTypeGroup = (RadioGroup) getLayoutInflater().inflate(
				R.layout.header_hall_type, null);
		// 初始化数据集合,为列表设置适配器
		mListItems = new ArrayList<Resource>();
		mAdapter = new CourseAdapter(this, mListItems);
		// 设置列表头部组件
		// mainList.addHeaderView(headTypeGroup);
		mainList.setAdapter(mAdapter);
	}

	private void queryContent() {
		if (!haveData) {
			return;
		}

		// 获取所有视频资源
		BmobQuery<Resource> query = new BmobQuery<Resource>();
		query.order("-createdAt");
		// query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		query.setLimit(Constant.NUMBERS_PER_PAGE);
		query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		query.addWhereLessThan("createdAt", date);
		query.addWhereEqualTo("classify", classifyName);
		if (type2 != null) {
			query.addWhereEqualTo("type", type2);
		}
		query.include("user.rank");
		query.findObjects(this, new FindListener<Resource>() {
			@Override
			public void onSuccess(List<Resource> list) {
				if (list.size() != 0 && list.get(list.size() - 1) != null) {
					if (isRefresh) {
						mListItems.clear();
					}
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();
					// 没有更过数据了
					if (list.size() < Constant.NUMBERS_PER_PAGE) {
						haveData = false;
					}
				} else {
					ToastFactory.showToast(mContext, "还没有内容,快发表第一个吧");
					haveData = false;
				}
				refreshList.onRefreshComplete();
			}

			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mContext, "获取数据失败,请检查网络" + arg1);
			}
		});
	}
	
	/**
	 * 设置列表属性及事件监听
	 */
	private void setListInfo() {
		refreshList.setMode(Mode.BOTH);
		refreshList.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		refreshList.getLoadingLayoutProxy(true, false).setReleaseLabel("松开刷新");
		refreshList.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"正在刷新");
		refreshList.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		refreshList.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("松开加载更多");
		refreshList.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载");
		refreshList.getLoadingLayoutProxy(true, true).setLoadingDrawable(
				getResources().getDrawable(R.drawable.refresh));
		// 下拉刷新
		refreshList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				pageNum = 0;
				haveData = true;
				isRefresh = true;
				
				    queryContent();
			}

			// 上拉加载更多
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (haveData) {
					isRefresh = false;					
					    queryContent();
				} else {
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
		// 列表点击事件
		mainList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(mContext, RecommendActivity.class);
				intent.putExtra("data", mListItems.get(position - 1));
				mContext.startActivity(intent);
			}
		});
	}
}
