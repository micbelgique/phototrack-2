package com.ulteam.phototrack.Helpers;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;

public class Help_Pixel {

	public static float getPixels(Resources resources, int dipSize) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipSize, resources.getDisplayMetrics());
	}
	
	public static float spToPixel(Resources resources, int spSize) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, resources.getDisplayMetrics());
	}

	public static int getStatusBarHeight(Activity activity) {
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int StatusBarHeight = rect.top;

		return StatusBarHeight;
	}
	
}
