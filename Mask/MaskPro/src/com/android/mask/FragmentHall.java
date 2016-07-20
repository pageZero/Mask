package com.android.mask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.adapter.CourseAdapter;
import com.android.recommend.RecommendActivity;
import com.android.settings.SettingsCenterActivity;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

public class FragmentHall extends Fragment {
	protected ArrayList<Resource> mListItems; //帖子列表
	private Context mContext;
	private ImageView btn_into_settings;
    private ImageView t1;
    private _User userInfo;
    private CourseAdapter mAdapter; //列表适配器
	private PullToRefreshListView refreshList; //列表
	private ListView mainList;
	private boolean haveData = true,isRefresh = false; //是否还有数据
	private int pageNum = 0; //当前加载到第几页
	private ArrayList<Resource> hotListItems; //热帖列表
	private boolean haveHotData = true;
	//SwitcherView的Button:
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private ImageView img4;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		return inflater.inflate(R.layout.activity_content1, container, false);
	}
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        initView(getActivity()); //初始化组件
        //为列表设置适配器
		mainList.setAdapter(mAdapter);
		//设置列表事件监听处理
		setListInfo();
		//设置进入界面刷新
		refreshList.setRefreshing(true);
	}
   public void  initView(Activity activity)
   {
   refreshList = (PullToRefreshListView) activity.findViewById(R.id.main_list);
   mainList = refreshList.getRefreshableView();
   //初始化数据集合和适配器
   mListItems = new ArrayList<Resource>();
   mAdapter = new CourseAdapter(mContext, mListItems);
   hotListItems = new ArrayList<Resource>(); //热帖列表
	img1 = (ImageView)activity. findViewById(R.id.id_img_switcherview1);
	img2 = (ImageView) activity.findViewById(R.id.id_img_switcherview2);
	img3 = (ImageView) activity.findViewById(R.id.id_img_switcherview3);
	img4 = (ImageView) activity.findViewById(R.id.id_img_switcherview4);
	queryHotContent();
   //上传按钮
   t1=(ImageView)activity.findViewById(R.id.to_upload_activity);
   t1.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
     Intent intent=new Intent(mContext,FileUpload.class);
     mContext.startActivity(intent);
		}
	});
   //个人信息按钮
    btn_into_settings = (ImageView) activity.findViewById(R.id.id_img_tolSmallMe);
	 userInfo = BmobUser.getCurrentUser(mContext,_User.class);
	 if(userInfo != null && userInfo.getHead() != null)
	 {
	 userInfo.getHead().loadImage(mContext, btn_into_settings);
	 }
	btn_into_settings.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext,
					SettingsCenterActivity.class);
			startActivity(intent);
		}
	});
   }
   private void queryHotContent(){
		BmobQuery<Resource> query = new BmobQuery<Resource>();
		query.order("-CommentNumber");
		//query.order("-createdAt");
		query.setLimit(4);
		query.include("user.rank");
		query.findObjects(mContext , new FindListener<Resource>() {
			@Override
			public void onSuccess(List<Resource> list) {
				Log.e("----", "size:"+list.size());
				if(list.size()!=0&&list.get(list.size()-1)!=null){					
					hotListItems.addAll(list);
					
					hotListItems.get(0).getPicture().loadImage(mContext, img1);
		    		hotListItems.get(1).getPicture().loadImage(mContext, img2);
		    		hotListItems.get(2).getPicture().loadImage(mContext, img3);
		    		hotListItems.get(3).getPicture().loadImage(mContext, img4);

		    		 Log.e("~~~~~", "list-size:"+hotListItems.size());
		    	        img1.setOnClickListener(new View.OnClickListener() {
		    	            @Override
		    	            public void onClick(View v) {
		    	                Intent intent = new Intent();
		    	                intent.setClass(mContext, RecommendActivity.class);
		    	                intent.putExtra("data", hotListItems.get(0));
		    	                mContext.startActivity(intent);
		    	            }
		    	        });
		    	        img2.setOnClickListener(new View.OnClickListener() {
		    	            @Override
		    	            public void onClick(View v) {
		    	                Intent intent = new Intent();
		    	                intent.setClass(mContext, RecommendActivity.class);
		    	                intent.putExtra("data", hotListItems.get(1));
		    	                mContext.startActivity(intent);
		    	            }
		    	        });
		    	        img3.setOnClickListener(new View.OnClickListener() {
		    	            @Override
		    	            public void onClick(View v) {
		    	                Intent intent = new Intent();
		    	                intent.setClass(mContext, RecommendActivity.class);
		    	                intent.putExtra("data", hotListItems.get(2));
		    	                mContext.startActivity(intent);
		    	            }
		    	        });
		    	        img4.setOnClickListener(new View.OnClickListener() {
		    	            @Override
		    	            public void onClick(View v) {
		    	                Intent intent = new Intent();
		    	                intent.setClass(mContext, RecommendActivity.class);
		    	                intent.putExtra("data", hotListItems.get(3));
		    	                mContext.startActivity(intent);
		    	            }
		    	        });
		    		
				}else{
					haveHotData = false;
				}
				
			}
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mContext, "获取数据失败,请检查网络"+arg1);
				haveHotData = false;
			}
		});
	}
   public void queryContent()
   {
	   if(!haveData){
			return;
		}
		
		BmobQuery<Resource> query = new BmobQuery<Resource>();
		query.order("-createdAt");
//		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		query.setLimit(Constant.NUMBERS_PER_PAGE);
		query.setSkip(Constant.NUMBERS_PER_PAGE*(pageNum++));
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		query.addWhereLessThan("createdAt", date);
		//query.addWhereEqualTo("classify", "整体"); //分类
		query.include("author.rank");
		query.findObjects(mContext, new FindListener<Resource>() {
			@Override
			public void onSuccess(List<Resource> list) {
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
					ToastFactory.showToast(mContext, "还没有内容,快发表第一个吧");
					haveData = false;
				}
				refreshList.onRefreshComplete();
			}
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(mContext, "获取数据失败,请检查网络"+arg1);
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
				Intent intent = new Intent();
				intent.setClass(mContext, RecommendActivity.class);
				intent.putExtra("data", mListItems.get(position-1));
				mContext.startActivity(intent);
			}
		});
	}
}
