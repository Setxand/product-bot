package com.productbot.utils;

public class QuickReplyUtils {

	public static String getCommonPayload(String basePayload) {
		return basePayload.substring(0, basePayload.indexOf("?"));
	}

	public static String[] getParams(String basePayload) {
		return basePayload.substring(basePayload.indexOf("?") + 1).split("&");
	}
}
