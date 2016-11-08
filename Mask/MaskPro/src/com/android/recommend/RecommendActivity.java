package com.android.recommend;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.adapter.CommentAdapter;
import com.android.mask.Constant;
import com.android.mask.R;
import com.android.settings.LoginActivity;
import com.bean.Collect;
import com.bean.Comment;
import com.bean.Resource;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecommendActivity extends Activity implements OnClickListener,
OnInfoListener, OnBufferingUpdateListener {
	private Resource aCourse = null;

	public Context context;
	public Context mContext;

	private ImageView img_returnHome;
	
	/*
	 * 视频播放相关
	 */
	private VideoView mVideoView; 
	private ProgressBar pb;
	private TextView downloadRateView, loadRateView;
	
	private TextView tv_MUname;
	private TextView tv_courseViewCount;
	private TextView tv_courseCollectCount;
	private ImageView img_courseCollectCount;
	private ImageView img_courseReport;

	private RelativeLayout comment;
	private TextView userName;	//楼主用户名
	private List<Comment> comments = new ArrayList<Comment>();
	private int pageNum = 0; //当前加载到第几页
	private boolean haveData=true,isRefresh=false;//是否还有数据
	private _User currentUser;
	
	private PullToRefreshListView refreshList; //pullListView
	private ListView commentList;	//评论列表
	private CommentAdapter mAdapter;
	
	private ArrayList<Resource> mListItems = new ArrayList<Resource>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //取消默认top bar
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.course); //绑定当前layout为activity_main.xml
		Vitamio.isInitialized(getApplicationContext());
		mContext = this;
		aCourse = (Resource) getIntent().getSerializableExtra("data");
		//初始化组件
		initView();
		//初始化视频播放区的内容
		initData();
		//设置适配器
		commentList.setAdapter(mAdapter);
		//设置pullList属性及事件
		setListInfo();
		//设置进入界面刷新,加载评论
		refreshList.setRefreshing();
		//设置该帖的浏览数量增加1个
		QiangYuDao dao = new QiangYuDao();
		dao.incrementLook(context, aCourse);
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		context = this;
		
		//返回主页事件
		img_returnHome = (ImageView) findViewById(R.id.id_img_returnHome);
		img_returnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				 Intent intent = new Intent();
	             intent.putExtra("result", " ");
	             RecommendActivity.this.setResult(RESULT_OK, intent);
	             RecommendActivity.this.finish();
			}
		});
		
		//绑定视频播放区按钮
		tv_MUname = (TextView) findViewById(R.id.id_tv_courseName);
		mVideoView = (VideoView) findViewById(R.id.id_videoV_course);
		pb = (ProgressBar) findViewById(R.id.probar);
		downloadRateView = (TextView) findViewById(R.id.download_rate);
		loadRateView = (TextView) findViewById(R.id.load_rate);
		
		tv_courseViewCount = (TextView) findViewById(R.id.id_tv_courseViewcount);
		tv_courseCollectCount = (TextView) findViewById(R.id.id_tv_courseCollectcount);
		img_courseCollectCount = (ImageView) findViewById(R.id.id_img_courseCollectcount);
		img_courseCollectCount.setOnClickListener(this);
		img_courseReport = (ImageView) findViewById(R.id.id_img_courseReport);
		img_courseReport.setOnClickListener(this);
		comment=(RelativeLayout)findViewById(R.id.comment);
		comment.setOnClickListener(this);
		tv_MUname.setText(aCourse.getResName());
		//初始化数据
		Uri uri = Uri.parse(aCourse.getResUrl()); 
		mVideoView.setVideoURI(uri);
		FrameLayout fl = (FrameLayout) findViewById(R.id.video_fl);
		MediaController mc = new MediaController(this,true,fl);
		mc.setVisibility(View.GONE);
		mVideoView.setMediaController(mc);
		mVideoView.requestFocus();
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnBufferingUpdateListener(this);
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
			}
		});
		
		tv_courseViewCount.setText(aCourse.getLookNumber().toString());
		tv_courseCollectCount.setText(aCourse.getCollectNum().toString());
		currentUser = BmobUser.getCurrentUser(context, _User.class);
		//得到组件
		refreshList = (PullToRefreshListView) findViewById(R.id.comment_list);
		commentList = refreshList.getRefreshableView();
		mAdapter = new CommentAdapter(context, comments);
	}
	
	private void fetchComment() {
		if(!haveData){
			return;
		}
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("comments", new BmobPointer(aCourse));
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
					ToastFactory.showToast(context, "还没有评论,快发表第一个吧");
					haveData = false;
				}
				refreshList.onRefreshComplete();
			}
			@Override
			public void onError(int arg0, String arg1) {
				refreshList.onRefreshComplete();
				ToastFactory.showToast(context, "加载评论失败："+arg1);
			}
		});
	}

	//设置列表属性及事件监听
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
			            	ToastFactory.showToast(context, "已加载完所有数据");
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

                if (currentUser == null) {
                    String text = "请先登录";
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                } else {
                    if (position == 0) {
                        return;
                    }

                    currentUser = BmobUser.getCurrentUser(context, _User.class);

                    String userId = comments.get(position - 1).getUser()
                            .getObjectId();
                    if (userId.equals(currentUser.getObjectId())) {
                        showDialog(comments.get(position - 1), new String[] {
                                "复制内容", "回复", "删除评论" });
                    } else {
                        showDialog(comments.get(position - 1), new String[] {
                                "复制内容", "回复" });
                    }
                }
            }
		});
	}	
		//显示列表点击的对话框
		private void showDialog(final Comment comment,final String[] stringItems){
	        final ActionSheetDialog menuDialog = new ActionSheetDialog(context, stringItems, null);
	        menuDialog.setOnOperItemClickL(new OnOperItemClickL() {
				@Override
				public void onOperItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					switch (position) {
					case 0:
						if(comment==null){
//							StringUtils.setClip(mContext, qiangYu.getContent());
						}else{
//							StringUtils.setClip(mContext, comment.getCommentContent());
						}
						break;
					case 1:
						if(stringItems.length>2){
							ToastFactory.showToast(context, "不能回复自己");
						}else{
							//跳转到高级回复界面
							Intent intent = new Intent();
							
							if(BmobUser.getCurrentUser(context)==null){
								intent.setClass(context, LoginActivity.class);
							}else{
								intent.setClass(context, aCoursePublishCommentActivity.class);
								intent.putExtra("qiangYu", aCourse);
								intent.putExtra("parentComment", comment);
							}
							context.startActivity(intent);
							break;
						}
						break;
					case 2:
						CommentDao dao = new CommentDao();
						dao.deleteComment(context, comment, aCourse, mAdapter);
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
			case R.id.id_img_courseReport:
			    if (currentUser ==  null) {
				Intent intent=new Intent(context, LoginActivity.class);
			    startActivity(intent);
			   } else {
				Resource.incrementReportNum(context, aCourse);
				ToastFactory.showToast(context, "举报");
			   }
			   break;
			case R.id.id_img_courseCollectcount:
				if (currentUser ==  null) {
					Intent intent=new Intent(context, LoginActivity.class);
				    startActivity(intent);
				} else {
					Resource.incrementCollectNum(context, aCourse);
					Collect c = new Collect(aCourse,currentUser);
					c.save(context, new SaveListener() {
						@Override
						public void onSuccess() {
							tv_courseCollectCount.setText(aCourse.getCollectNum()+1+"");
							ToastFactory.showToast(context, "收藏成功");
						}
						@Override
						public void onFailure(int arg0, String arg1) {
							Log.e("-------", "收藏失败！");
						}
					});
				}
			break;
			case R.id.comment:
				haveData = true;
				//跳转到高级回复界面
				Intent intent = new Intent();
				if(BmobUser.getCurrentUser(context)==null){
					intent.setClass(context, LoginActivity.class);
				}else{
					intent.setClass(context, aCoursePublishCommentActivity.class);
					intent.putExtra("qiangYu", aCourse);
				}
				context.startActivity(intent);
				break;
			default: break;
			}
		}

		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			loadRateView.setText(percent + "%");
		}

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			switch (what) {
		    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
		      if (mVideoView.isPlaying()) {
		        mVideoView.pause();
		        pb.setVisibility(View.VISIBLE);
		        downloadRateView.setText("");
		        loadRateView.setText("");
		        downloadRateView.setVisibility(View.VISIBLE);
		        loadRateView.setVisibility(View.VISIBLE);

		      }
		      break;
		    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
		      mVideoView.start();
		      pb.setVisibility(View.GONE);
		      downloadRateView.setVisibility(View.GONE);
		      loadRateView.setVisibility(View.GONE);
		      break;
		    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
		      downloadRateView.setText("" + extra + "kb/s" + "  ");
		      break;
		    }
		    return true;
		}
	
}