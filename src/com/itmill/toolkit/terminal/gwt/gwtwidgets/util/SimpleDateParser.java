package com.itmill.toolkit.terminal.gwt.gwtwidgets.util;

import java.util.Date;

import com.itmill.toolkit.terminal.gwt.client.DateTimeService;
import com.itmill.toolkit.terminal.gwt.gwtwidgets.util.regex.Pattern;

/**
 * This is a simple regular expression based parser for date notations.
 * While our aim is to fully support in the future the JDK date parser, currently
 * only numeric notations and literals are supported such as <code>dd/MM/yyyy HH:mm:ss.SSSS</code>.
 * Each entity is parsed with the same number of digits, i.e. for <code>dd</code> two digits will be
 * parsed while for <code>d</code> only one will be parsed.
 * @author <a href="mailto:g.georgovassilis@gmail.com">George Georgovassilis</a>
 *
 */

public class SimpleDateParser {


	private final static String DAY_IN_MONTH = "d";

	private final static String MONTH = "M";

	private final static String YEAR = "y";

	private final static String LITERAL = "\\";

	private final static int DATE_PATTERN = 0;

	private final static int REGEX_PATTERN = 1;

	private final static int COMPONENT = 2;

	private final static int REGEX = 0;

	private final static int INSTRUCTION = 1;

	private final static String[] TOKENS[] = {
	{ "SSSS", "(\\d\\d\\d\\d)",DateLocale.TOKEN_MILLISECOND }, 
	{ "SSS", "(\\d\\d\\d)", DateLocale.TOKEN_MILLISECOND },
	{ "SS", "(\\d\\d)", DateLocale.TOKEN_MILLISECOND }, 
	{ "S", "(\\d)", DateLocale.TOKEN_MILLISECOND },
	{ "ss", "(\\d\\d)", DateLocale.TOKEN_SECOND }, 
	{ "s", "(\\d\\d)", DateLocale.TOKEN_SECOND },
	{ "mm", "(\\d\\d)", DateLocale.TOKEN_MINUTE }, 
	{ "m", "(\\d\\d)", DateLocale.TOKEN_MINUTE},
	{ "HH", "(\\d\\d)", DateLocale.TOKEN_HOUR_24},
	{ "H", "(\\d{1,2})", DateLocale.TOKEN_HOUR_24 },
	{ "hh", "(\\d\\d)", DateLocale.TOKEN_HOUR_12},
	{ "h", "(\\d{1,2})", DateLocale.TOKEN_HOUR_12 },
	{ "dd", "(\\d\\d)", DateLocale.TOKEN_DAY_OF_MONTH }, 
	{ "d", "(\\d{1,2})", DateLocale.TOKEN_DAY_OF_MONTH },
	{ "MM", "(\\d\\d)", DateLocale.TOKEN_MONTH }, 
	{ "M", "(\\d{1,2})", DateLocale.TOKEN_MONTH },
	{ "yyyy", "(\\d\\d\\d\\d)", DateLocale.TOKEN_YEAR }, 
	{ "yyy", "(\\d\\d\\d\\d)", DateLocale.TOKEN_YEAR },
	{ "yy", "(\\d\\d\\d\\d)", DateLocale.TOKEN_YEAR }, 
	{ "y", "(\\d{1,2})", DateLocale.TOKEN_YEAR },
	{ "a", "(\\S{1,4})", DateLocale.TOKEN_AM_PM }
	};

	private Pattern regularExpression;

	private String instructions = "";

	private static void _parse(String format, String[] args) {
		if (format.length() == 0)
			return;
		if (format.startsWith("'")){
			format = format.substring(1);
			int end = format.indexOf("'");
			if (end == -1)
				throw new IllegalArgumentException("Unmatched single quotes.");
			args[REGEX]+=Pattern.quote(format.substring(0,end));
			format = format.substring(end+1);
		}
		for (int i = 0; i < TOKENS.length; i++) {
			String[] row = TOKENS[i];
			String datePattern = row[DATE_PATTERN];
			if (!format.startsWith(datePattern))
				continue;
			format = format.substring(datePattern.length());
			args[REGEX] += row[REGEX_PATTERN];
			args[INSTRUCTION] += row[COMPONENT];
			_parse(format, args);
			return;
		}
		args[REGEX] += Pattern.quote(""+format.charAt(0));
		format = format.substring(1);
		_parse(format, args);
	}

	private static void load(Date date, String text, String component, String input, Pattern regex) {
		if (component.equals(DateLocale.TOKEN_MILLISECOND)) {
			// TODO implement setMilliseconds to date object
		}

		if (component.equals(DateLocale.TOKEN_SECOND)) {
			date.setSeconds(Integer.parseInt(text));
		}

		if (component.equals(DateLocale.TOKEN_MINUTE)) {
			date.setMinutes(Integer.parseInt(text));
		}

		if (component.equals(DateLocale.TOKEN_HOUR_24)) {
			date.setHours(Integer.parseInt(text));
		}
		
		if (component.equals(DateLocale.TOKEN_HOUR_12)) {
			int h = Integer.parseInt(text);
			String token = com.itmill.toolkit.terminal.gwt.client.DateLocale.getPM();
			String which = input.substring(input.length() - token.length()); // Assumes both AM and PM tokens have same length
			if(which.equals(token))
				h += 12;
			date.setHours(h);
		}

		if (component.equals(DateLocale.TOKEN_DAY_OF_MONTH)) {
			date.setDate(Integer.parseInt(text));
		}
		if (component.equals(DateLocale.TOKEN_MONTH)) {
			date.setMonth(Integer.parseInt(text)-1);
		}
		if (component.equals(DateLocale.TOKEN_YEAR)) {
			//TODO: fix for short patterns
			date.setYear(Integer.parseInt(text)-1900);
		}

	}

	public SimpleDateParser(String format) {
		String[] args = new String[] { "", "" };
		_parse(format, args);
		regularExpression = new Pattern(args[REGEX]);
		instructions = args[INSTRUCTION];
	}

	public Date parse(String input) {
		Date date = new Date(0, 0, 0, 0, 0, 0);
		String matches[] = regularExpression.match(input);
		if (matches == null)
			throw new IllegalArgumentException(input+" does not match "+regularExpression.pattern());
		if (matches.length-1!=instructions.length())
			throw new IllegalArgumentException("Different group count - "+input+" does not match "+regularExpression.pattern());
		for (int group = 0; group < instructions.length(); group++) {
			String match = matches[group + 1];
			load(date, match, ""+instructions.charAt(group), input, regularExpression);
		}
		return date;
	}
	
	public static Date parse(String input, String pattern){
		return new SimpleDateParser(pattern).parse(input);
	}
}
