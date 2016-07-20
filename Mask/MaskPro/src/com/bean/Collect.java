package com.bean;

import android.content.Context;
import android.util.Log;

import com.util.ToastFactory;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;

public class Collect extends BmobObject{

	private Resource resourceId;//被收藏的资源
	private _User userId;//用户
	
	public Collect() {
	}
	public Collect(Resource r, _User u) {
		this.resourceId = r;
		this.userId = u;
	}
	public Resource getResourceId() {
		return resourceId;
	}
	public void setResourceId(Resource resourceId) {
		this.resourceId = resourceId;
	}
	public _User getUserId() {
		return userId;
	}
	public void setUserId(_User userId) {
		this.userId = userId;
	}
	
	public static void deleteCollect(final Context mContext, Collect collect) {
		
	//	Collect c = new Collect();
	//	c.setObjectId(collect.getObjectId());
		collect.delete(mContext,new DeleteListener() {
        @Override
        public void onSuccess() {
        	ToastFactory.showToast(mContext, "删除收藏成功");
        }
        @Override
        public void onFailure(int i, String s) {
        	ToastFactory.showToast(mContext, "删除收藏失败"+s);
        	Log.e("------", "删除收藏失败"+s);
        }
    });	
	}
	
}
