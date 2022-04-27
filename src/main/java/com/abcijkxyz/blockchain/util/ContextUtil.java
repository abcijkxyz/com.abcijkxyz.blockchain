package com.abcijkxyz.blockchain.util;

public class ContextUtil {
	public static ThreadLocal<Context> context = new ThreadLocal<Context>();

	public static Context getContext() {
		return context.get();
	}

	public static void setContext(Context context) {
		ContextUtil.context.set(context);
	}

}
