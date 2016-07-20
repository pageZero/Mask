package com.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtil {
	// 圆角显示

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		// Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(),
		// bitmap.getHeight(), Config.ARGB_8888);
		// Canvas canvas = new Canvas(roundBitmap);
		// int color = 0xff424242;
		// Paint paint = new Paint();
		// //设置圆形半径
		// int radius;
		// if(bitmap.getWidth()>bitmap.getHeight()) {
		// radius = bitmap.getHeight()/2;
		// }
		// else {
		// radius = bitmap.getWidth()/2;
		// }
		// //绘制圆形
		// paint.setAntiAlias(true);
		// canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(color);
		// canvas.drawCircle( bitmap.getWidth()/ 2, bitmap.getHeight() / 2,
		// radius, paint);
		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// canvas.drawBitmap(bitmap, 0, 0, paint);
		// return roundBitmap;

		// 裁剪为圆形
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float radius;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		//长宽是一样的
		radius = width / 2;
		top = 0;
		bottom = width;
		left = 0;
		right = width;
		height = width;
		dst_left = 0;
		dst_top = 0;
		dst_right = width;
		dst_bottom = width;
	
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
}