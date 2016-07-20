package com.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Environment;

public class BitmapUtil {
	/**
	 * 加载资源图片为bitmap
	 */
	public Bitmap decodeResource(Context context,int resourceId){
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(),resourceId);
		return temp;
	}
	/**
	 * 加载sd卡的图片为bitmap
	 */
	public Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}
	/**
	 * 保存bitmap到sd卡
	 */
	public String saveToSdCard(Bitmap bitmap,Context mContext,String dateTime) {
		String files = CacheUtils.getCacheDirectory(mContext, true, "pic")+ dateTime + "_11.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
	
	public static String saveBitmap2file(Bitmap bmp,String filename){
		CompressFormat format= Bitmap.CompressFormat.JPEG;
		int quality = 100;
		File f = new File(Environment.getExternalStorageDirectory(),filename+".jpg");
		if (f.exists())
			f.delete();
		OutputStream stream = null;
		try {
			
			stream = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bmp.compress(format, quality, stream))
			return Environment.getExternalStorageDirectory().toString()+"/"+filename+".jpg";
		return null;
		}
}
