package com.yihai.caotang.utils;

public class StringUtils {
	public static boolean isNullOrEmpty(CharSequence text) {
		if (text == null || text.length() == 0) {
			return true;
		}
		return false;
	}
}
