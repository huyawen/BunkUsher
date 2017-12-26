package com.meiaomei.bankusher.utils;

import android.content.Context;
import android.util.TypedValue;

import com.meiaomei.bankusher.BankUsherApplication;

/**
 * 屏幕视频转换类
 */

public class DensityUtil {

	/**
	 * dp转px
	 *
	 * @param context
	 * @param dpVal
	 * @return
	 */
	public static int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
				.getDisplayMetrics());
	}


	/**
	 * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context paramContext, float paramFloat) {
		return (int) (0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
	}

	public static int dip2px(float paramFloat) {
		return (int) (0.5F + paramFloat * BankUsherApplication.getAppContext().getResources().getDisplayMetrics().density);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context paramContext, float paramFloat) {
		return (int) (0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().density);
	}

	public static int px2dip(float paramFloat) {
		return (int) (0.5F + paramFloat * BankUsherApplication.getAppContext().getResources().getDisplayMetrics().density);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int px2sp(float pxValue) {
		final float fontScale = BankUsherApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int sp2px(float spValue) {
		final float fontScale = BankUsherApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


}
