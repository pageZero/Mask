package com.dao;

import com.adapter.AIContentAdapter;
import com.myview.MyProgressDialog;
import com.bean.Resource;
import com.bean.Topic;
import com.util.ToastFactory;

import android.content.Context;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;


public class QiangYuDao {
	/**
	 * 删除帖子
	 */
	public void deleteQiangYu(final Context mContext, final Topic qiangYu,
			final AIContentAdapter mAdapter) {
		final MyProgressDialog pDialog = new MyProgressDialog(mContext);
		pDialog.setMsg("正在删除");
		pDialog.show();
		//删除帖子中的图片
		BmobFile oldFile = qiangYu.getContentfigureurl();
		if (oldFile != null) {
			oldFile.delete(mContext, new DeleteListener() {
				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int arg0, String arg1) {
				}
			});
		}
		//删除帖子数据
		qiangYu.delete(mContext, new DeleteListener() {
			@Override
			public void onSuccess() {
				if(mAdapter!=null){
					mAdapter.getDataList().remove(qiangYu);
					mAdapter.notifyDataSetChanged();
				}
				pDialog.dismiss();
				ToastFactory.showToast(mContext, "删除成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				pDialog.dismiss();
				ToastFactory.showToast(mContext, "删除失败");
			}
		});
	}
	
	/**
	 * 设置浏览数量增加一个
	 */
	public void incrementLook(Context mContext,Topic qiangYu){
		qiangYu.increment("lookNumber");
		qiangYu.update(mContext);
	}
	
	//设置浏览数量增加一个
	public void incrementLook(Context mContext,Resource aCourse){
		aCourse.increment("lookNumber");
		aCourse.update(mContext);
	}
}
