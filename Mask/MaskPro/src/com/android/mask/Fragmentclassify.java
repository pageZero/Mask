package com.android.mask;

import com.bean._User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.android.classify.EachClassifyActivity;
import com.android.classify.PictureAdapter;
import com.android.mask.R;
import com.android.settings.SettingsCenterActivity;
import com.android.topic.CustomApplcation;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import search.SearchActivity;


public class Fragmentclassify extends Fragment {
	private ImageView btn_into_settings;
	private _User userInfo;
	private ImageView btn_into_search;
	private Context mContext;
	
	private GridView gridView;
	//图片的文字标题
	private String[] titles = new String[]{
			"整体","眼妆","眉妆","唇妆","修容","卸妆"
	};
	//图片Id数组
	private int[] images = new int[] {
			R.drawable.classify_overall,
			R.drawable.classify_eye,
			R.drawable.classify_eyebrow,
			R.drawable.classify_lips,
			R.drawable.classify_decorate,
			R.drawable.classify_remove,
	};
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.activity_content2,container, false);
		 mContext = getActivity();
		 gridView = (GridView) v.findViewById(R.id.gridview);
		 PictureAdapter picAdapter = new PictureAdapter(titles,images,mContext);
		 gridView.setAdapter(picAdapter);
		 gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//Toast.makeText(ClassifyActivity.this, "pic" + (position+1), Toast.LENGTH_SHORT).show(); 
					Intent intent = new Intent();
					intent.putExtra("类名", titles[position]);
					intent.setClass(mContext, EachClassifyActivity.class);
					startActivityForResult(intent, position);
				}
		 });
	     btn_into_settings = (ImageView) v.findViewById(R.id.id_img_tolSmallMe);
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
		
		btn_into_search = (ImageView) v.findViewById(R.id.id_img_search);
		btn_into_search.setOnClickListener(new View.OnClickListener() {
				@Override
		public void onClick(View v) {				
		Intent intent=new Intent(mContext, SearchActivity.class);
		mContext.startActivity(intent);				
				}
			});
		return v;
}
}