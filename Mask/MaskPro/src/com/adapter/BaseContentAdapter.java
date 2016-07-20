package com.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 列表适配器的基本抽象类
 */
public abstract class BaseContentAdapter<T> extends BaseAdapter {

	protected Context mContext; // 上下文对象
	protected List<T> dataList; // 数据集合
	protected LayoutInflater mInflater; // 加载布局的对象

	/**
	 * 构造方法,初始化上下文对象和数据集合
	 */
	public BaseContentAdapter(Context context, List<T> list) {
		mContext = context;
		dataList = list;
		// 得到加载布局对象的方法
		mInflater = LayoutInflater.from(mContext);
	}

	/*
	 * 得到数据集合
	 */
	public List<T> getDataList() {
		return dataList;
	}

	/*
	 * 设置数据集合
	 */
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public T getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getConvertView(position, convertView, parent);
	}
	/**
	 * 得到每一项布局的抽象方法,由子类实现
	 */
	public abstract View getConvertView(int position, View convertView,
			ViewGroup parent);

}
