package it.feio.android.omninotes.utils;

import android.util.Log;

public class WiHelper {
	public static void logd(String logstr) {
		// debuggable 是关闭的，调试时的时候，把 Log.d 改成 Log.e，
		// 提交入库的时候，改回 Log.d，避免发布办法打印log
		Log.d("wiii", logstr);
	}
}
