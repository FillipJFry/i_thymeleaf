package com.goit.fry.thymeleaf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UTCHandler {

	private static final Pattern utcPattern = Pattern.compile("UTC([+\\-][1-9]+)$");

	public static boolean presentUTCstr(String tzStr) {

		return utcPattern.matcher(tzStr).find();
	}

	public static String replaceIfNecessary(String tzStr) {

		Matcher m = utcPattern.matcher(tzStr);
		if (m.find())
			return "GMT" + m.group(1);

		return tzStr;
	}
}
