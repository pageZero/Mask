package com.android.classify;

import java.util.ArrayList;
import java.util.List;

import com.android.mask.R;

import android.content.Context;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<MyPicture> pictures;
	
	public PictureAdapter(String[] title,int[] images, Context context){
		super();
		pictures = new ArrayList<MyPicture>();
		inflater = LayoutInflater.from(context);
		for (int i = 0; i < images.length; i++) {
			MyPicture pic = new MyPicture(title[i], images[i]);
			pictures.add(pic);
		}
	}
	
	@Override
	public int getCount() {
		if (pictures != null) {
			return pictures.size();
		} else 
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.class_pic_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			
			viewHolder.imgbtn = (ImageView) convertView.findViewById(R.id.classify_item_img);
			convertView.setTag(viewHolder); 
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.title.setText(pictures.get(position).getTitle());
		viewHolder.imgbtn.setBackgroundResource(pictures.get(position).getImageId());
		
		return convertView;
	}
	
	class ViewHolder{
		public TextView title;
		public ImageView imgbtn;
	}
	
	class MyPicture extends Picture{
		private String title;
		private int imageId;
		
		public MyPicture() {
			super();
		}
		public MyPicture(String title, int imgId) {
			super();
			this.setTitle(title);
			this.setImageId(imgId);
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getImageId() {
			return imageId;
		}
		public void setImageId(int imageId) {
			this.imageId = imageId;
		}
	}

}
