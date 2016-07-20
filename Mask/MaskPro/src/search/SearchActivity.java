package search;

 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import cn.bmob.v3.BmobQuery;
 import cn.bmob.v3.datatype.BmobDate;
 import cn.bmob.v3.listener.FindListener;
 import com.adapter.CourseAdapter;
 import com.android.mask.Constant;
 import com.android.mask.R;
 import com.android.recommend.RecommendActivity;
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
	
import android.util.Log;
	import android.view.View;
import android.view.Window;
	
	import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
	
	import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

	public class SearchActivity extends Activity {
		private ImageView to_classifyActivity;
		private String keyWord;
		private EditText searchStr;
		private TextView search;
		

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
		

		// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// 1~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		private _User userInfo;

		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消默认top bar
			
			setContentView(R.layout.search_course); // 绑定当前layout为activity_main.xml

			// 1~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			initView(); // 初始化组件
			
			// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}

		// 1~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		private void initView() {
			mContext = this;
			
			
			//返回按钮点击事件
			to_classifyActivity = (ImageView) findViewById(R.id.return_classify);
			to_classifyActivity.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {				
					finish();
				}
			});
		
			
			//搜索点击事件
			search = (TextView) findViewById(R.id.id_tv_search);
			searchStr = (EditText) findViewById(R.id.id_et_search);
			search.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String wantSearchStr = searchStr.getText().toString();
					Log.e("-----","wantSearchStr="+wantSearchStr);
					if(wantSearchStr == null || wantSearchStr.equals("")){
						ToastFactory.showToast(mContext, "请输入内容");
					}else{
						keyWord = wantSearchStr;
						// 设置列表事件监听处理
						setListInfo();
						// 设置进入界面刷新
						refreshList.setRefreshing(true);
					}
				}
			});
			
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
			query.addWhereContains("labels", keyWord);
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
			// 选择类型的监听事件，筛选大厅帖子
			headTypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					switch (arg1) {
					case R.id.hall_type_radio1:
						type2 = null;
						break;
					case R.id.hall_type_radio2:
						type2 = Constant.TYPE_1;
						break;
					case R.id.hall_type_radio3:
						type2 = Constant.TYPE_2;
						break;
					case R.id.hall_type_radio4:
						type2 = Constant.TYPE_3;
						break;
					default:
						break;
					}
					haveData = true;
					pageNum = 0;
					mListItems.clear();
					mAdapter.notifyDataSetChanged();
					refreshList.setRefreshing();
				}
			});
		

		// 2~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
