package com.android.mask;

import java.util.HashMap;

import com.android.mask.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

 /**
 * 自定义适配器，在gridView中添加文本和复选框，在getView里设置选中item时的动作
 * @author zjl
 */
public class MyCheckAdapter extends BaseAdapter {

	public HashMap<Integer, Boolean> isSelected;
	private Context context;
	private String[] data;

	public MyCheckAdapter(Context context, String[] list) {
		this.context = context;
		this.data = list;

		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < data.length; i++) {
			isSelected.put(i, false);
		}// 初始化hashmap
	}

	/**
	 * 返回GridView的item的数目，根据getItem一条一条的显示，有多少条item就调用多少次getView
	 */
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Log.i("MyCheckAdapter", "----------------------------------");
		MyViewHolder holder = null;

		if (view == null) {
			holder = new MyViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.file_upload_label_item, null);
			holder.tv = (TextView) view.findViewById(R.id.item_tv);
			holder.cb = (CheckBox) view.findViewById(R.id.item_cb);

			view.setTag(holder);

		} else {
			holder = (MyViewHolder) view.getTag();
		}

		holder.tv.setText(data[position]);
		holder.cb.setChecked(isSelected.get(position));
		return view;
	}

}

class MyViewHolder {
	public CheckBox cb;
	public TextView tv;
	public LinearLayout layout;
}
