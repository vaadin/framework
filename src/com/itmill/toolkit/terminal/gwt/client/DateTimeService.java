package com.itmill.toolkit.terminal.gwt.client;

import java.util.Date;

/**
 * This class provides date/time parsing services to 
 * all components on the client side.
 * 
 * @author IT Mill Ltd.
 *
 */
public class DateTimeService {
	
	private String currentLocale;
	
	private static int [] maxDaysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	/**
	 * Creates a new date time service with the application default locale.
	 */
	public DateTimeService() {
		currentLocale = LocaleService.getDefaultLocale();
	}
	
	/**
	 * Creates a new date time service with a given locale.
	 * 
	 * @param locale e.g. fi, en etc.
	 * @throws LocaleNotLoadedException
	 */
	public DateTimeService(String locale) throws LocaleNotLoadedException {
		setLocale(locale);
	}
	
	public void setLocale(String locale) throws LocaleNotLoadedException {
		if(LocaleService.getAvailableLocales().contains(locale))
			currentLocale = locale;
		else throw new LocaleNotLoadedException(locale);
	}
	
	public String getLocale() {
		return currentLocale;
	}
	
	public String getMonth(int month) {
		try {
			return LocaleService.getMonthNames(currentLocale)[month];
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return null;
	}
	
	public String getShortMonth(int month) {
		try {
			return LocaleService.getShortMonthNames(currentLocale)[month];
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return null;
	}
	
	public String getDay(int day) {
		try {
			return LocaleService.getDayNames(currentLocale)[day];
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return null;
	}
	
	public String getShortDay(int day) {
		try {
			return LocaleService.getShortDayNames(currentLocale)[day];
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return null;
	}
	
	public int getFirstDayOfWeek() {
		try {
			return LocaleService.getFirstDayOfWeek(currentLocale);
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return 0;
	}
	
	public boolean isTwelveHourClock() {
		try {
			return LocaleService.isTwelveHourClock(currentLocale);
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return false;
	}
	
	public String getClockDelimeter() {
		try {
			return LocaleService.getClockDelimiter(currentLocale);
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		return ":";
	}
	
	public String[] getAmPmStrings() {
		try {
			return LocaleService.getAmPmStrings(currentLocale);
		} catch (LocaleNotLoadedException e) {
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		String[] temp = new String[2];
		temp[0] = "AM";
		temp[1] = "PM";
		return temp;
	}
	
	public int getStartWeekDay(Date date){
		Date dateForFirstOfThisMonth = new Date(date.getYear(), date.getMonth(), 1);
		int firstDay;
		try {
			firstDay = LocaleService.getFirstDayOfWeek(currentLocale);
		} catch (LocaleNotLoadedException e) {
			firstDay = 0;
			// TODO redirect to console
			System.out.println(e + ":" + e.getMessage());
		}
		int start = dateForFirstOfThisMonth.getDay() - firstDay;
		if(start < 0) start = 6;
		return start;
	}
	
	public static int getNumberOfDaysInMonth(Date date){
		int month = date.getMonth();
		if(month == 1 && true == isLeapYear(date))
			return 29;
		return maxDaysInMonth[month]; 
	}
	
	public static boolean isLeapYear(Date date){
		// Instantiate the date for 1st March of that year
		Date firstMarch = new Date(date.getYear(), 2, 1);

		// Go back 1 day
		long firstMarchTime = firstMarch.getTime();
		long lastDayTimeFeb = firstMarchTime - (24*60*60*1000); // NUM_MILLISECS_A_DAY
		
		//Instantiate new Date with this time
		Date febLastDay = new Date(lastDayTimeFeb);
		
		// Check for date in this new instance
		return (29 == febLastDay.getDate()) ? true : false;
	}

}
