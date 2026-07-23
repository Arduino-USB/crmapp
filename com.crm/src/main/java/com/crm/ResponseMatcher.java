package com.crm;

import com.crm.entites.Response;

public class ResponseMatcher {

	public static boolean matches(
			Response response,
			String message) {

		switch (response.getMode()) {

		case EQUALS:
			return message.equals(response.getText());

		case EQUALS_IGNORE_CASE:
			return message.equalsIgnoreCase(response.getText());

		case STARTS_WITH:
			return message.startsWith(response.getText());

		case ENDS_WITH:
			return message.endsWith(response.getText());

		case CONTAINS:
			return message.contains(response.getText());

		case REGEX:
			return message.matches(response.getText());

		default:
			return false;
		}
	}
}
