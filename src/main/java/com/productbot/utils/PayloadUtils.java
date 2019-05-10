package com.productbot.utils;

import java.util.Arrays;
import java.util.List;

public class PayloadUtils {

	public static String getCommonPayload(String basePayload) {
		return basePayload.contains("?") ? basePayload.substring(0, basePayload.indexOf("?")) : basePayload;
	}

	public static String[] getParams(String basePayload) {
		return basePayload.substring(basePayload.indexOf("?") + 1).split("&");
	}

	public static String createPayloadWithParams(String payload, List<String> params) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(payload).append("?");
		params.forEach(p -> stringBuilder.append(p).append("&"));
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	public static String createPayloadWithParams(String payload, String... params) {
		return createPayloadWithParams(payload, Arrays.asList(params));
	}
}
