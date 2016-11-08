package com.android.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.adapter.SmallCourseAdapter;
import com.android.mask.Constant;
import com.android.mask.MainActivity1;
import com.android.mask.R;
import com.android.recommend.RecommendActivity;
import com.bean.Collect;
import com.bean.Resource;
import com.bean._User;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.util.ToastFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyCollection extends Activity{
	
	private ArrayList<Collect> myCollects = new ArrayList<Collect>();//获取到的收藏记录
	private ArrayList<Resource> myResourcesList;//视频资源列表
	private SmallCourseAdapter myAdapter;//适配器
	private PullToRefreshListView refreshList; //列表
	private ListView resourceList;
	private int pageNum = 0; //当前加载到第几页
	private boolean haveData = true,isRefresh = false; //是否还有数据
	
	private TextView title;// top_bar上面的文字提示
	private RelativeLayout return_settings_center;//返回按钮
	private Context mycollection_context;
	
	private LinearLayout bg_logo_layout;
	private TextView tips;//提示
	private LinearLayout list_show;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.my_collections);
		init();
		setListener();
		setListInfo();
		//设置进入界面刷新
		refreshList.setRefreshing(true);
	}
	
	
	/**
	 * 初始化控件
	 */
	private void init() {
		mycollection_context = this;
		title = (TextView) findViewById(R.id.top_bar_title);
		title.setText("我的收藏");
		return_settings_center = (RelativeLayout) findViewById(R.id.return_layout);
		
		refreshList = (PullToRefreshListView) findViewById(R.id.listView_my_collections);
		resourceList = refreshList.getRefreshableView();
		//初始化列表数据，为列表绑定适配器
		myResourcesList = new ArrayList<Resource>();
		//初始化适配器
		myAdapter = new SmallCourseAdapter(mycollection_context, myResourcesList);
		//为列表绑定适配器
		resourceList.setAdapter(myAdapter);
		
		list_show = (LinearLayout) findViewById(R.id.list);
		bg_logo_layout = (LinearLayout) findViewById(R.id.bg_logo_layout);
		tips = (TextView) findViewById(R.id.text_tips);//没有收藏时的文字提示
		tips.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mycollection_context, MainActivity1.class);
				finish();
			    startActivity(intent);
			}
		});
	}
	//为按钮添加单击事件
	private void setListener() {
		return_settings_center.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyCollection.this.finish();
			}
		});
		
	}
	/**
	 * 根据当前用户查询她收藏的资源
	 */
	private void queryResource() {
		if (!haveData) {
			return;
		}
		_User userInfo = BmobUser.getCurrentUser(mycollection_context, _User.class);
		BmobQuery<Collect> query = new BmobQuery<Collect>();
		query.order("-createdAt");
		query.setLimit(Constant.NUMBERS_PER_PAGE);
		query.setSkip(Constant.NUMBERS_PER_PAGE*(pageNum++));
		query.include("resourceId");
		query.addWhereEqualTo("userId", new BmobPointer(userInfo));
		query.findObjects(mycollection_context, new FindListener<Collect>() {
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mycollection_context, "加载收藏失败："+arg1);
			}
			@Override
			public void onSuccess(List<Collect> data) {
				Log.e("---------", "--------data size:"+data.size());
				if (data.size() != 0 && data.get(data.size()-1)!=null) {
					if (isRefresh) {
						myCollects.clear();
						myResourcesList.clear();
					}
					myCollects.addAll(data);
					Iterator it = myCollects.iterator();
					while (it.hasNext()) {//获取到收藏列表中收藏的资源
						myResourcesList.add(((Collect)it.next()).getResourceId());
					}
					myAdapter.notifyDataSetChanged();//通知适配器更新数据
					if(data.size()<Constant.NUMBERS_PER_PAGE){
						haveData = false;
					}	
				} else {
					ToastFactory.showToast(mycollection_context, "还没有收藏，快收藏一个吧");
					haveData = false;
				//	list_show.setVisibility(View.GONE);
					bg_logo_layout.setVisibility(View.VISIBLE);
				}
				refreshList.onRefreshComplete();
				
			}
			
		});
	}
	
	/**
	 * 设置列表的属性及事件监听
	 */
	private void setListInfo() {
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
				queryResource();
			}
			//上拉加载更多
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(haveData){
					isRefresh = false;
					queryResource();
				}else{
					refreshList.postDelayed(new Runnable() {
			            @Override
			            public void run() {
			            	ToastFactory.showToast(mycollection_context, "已加载完所有数据");
			            	refreshList.onRefreshComplete();
			            }
			        }, 500);
				}
			}
	    });
		
		//列表点击事件
		resourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				showDialog(myResourcesList.get(position - 1),new String[]{"查看","删除"});
			}
		});
	}

	/**
	 * 显示列表点击的对话框
	 * @param resource 列表对应的资源
	 * @param strings 对话框的提示操作数组
	 */
	protected void showDialog(final Resource resource, String[] stringItems) {
		 final ActionSheetDialog menuDialog = new ActionSheetDialog(mycollection_context, stringItems, null);
		 menuDialog.setOnOperItemClickL(new OnOperItemClickL() {
			@Override
			public void onOperItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
				case 0: //查看，把资源对象当做参数传递给具体显示页面
					Resource.incrementlookNumber(mycollection_context, resource);
					Log.e("-------", "查看");
					Intent intent = new Intent();
					intent.setClass(mycollection_context, RecommendActivity.class);
					intent.putExtra("data", resource);
					finish();
					mycollection_context.startActivity(intent);
					
					break;
				case 1://删除，传入资源对象，用Resource的静态方法删除
					Log.e("-------", "----------删除");
					//资源收藏量-1
					Resource.decrementCollect(mycollection_context, resource);
					int index = myResourcesList.indexOf(resource);
					Log.e("-----", "zzzzzzzzz"+index);
					Collect aCollect = myCollects.get(index);
					Log.e("-----", "zzzzzzzzz"+myCollects.size()+aCollect.getObjectId());
					Collect.deleteCollect(mycollection_context, aCollect);
					refreshList.setRefreshing(true);
					myAdapter.notifyDataSetChanged();//通知适配器更新数据
					break;
				}
				menuDialog.dismiss();
			}
		 });
		 menuDialog.isTitleShow(false).show();
	}

}
