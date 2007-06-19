package com.itmill.toolkit.terminal.gwt.client;

/**
 * This class provides date/time parsing services to all components.
 * 
 * @author Jouni Koivuviita
 *
 */
public class DateTimeService {
	
	private LocaleService localeService;
	
	private String currentLocale;
	
	public DateTimeService(Client client) {
		localeService = new LocaleService(client);
	}
	
	public DateTimeService(Client client, String locale) {
		this(client);
		setLocale(locale);
	}
	
	public void setLocale(String locale) {
		currentLocale = locale;
	}
	
	public String getLocale() {
		return currentLocale;
	}
	
	public String getMonth(int month) throws Exception {
		if(currentLocale != null)
			return localeService.getMonthNames(currentLocale)[month];
		else throw new Exception("No locale specified.");
	}
	
	public String getShortMonth(int month) throws Exception {
		if(currentLocale != null)
			return localeService.getShortMonthNames(currentLocale)[month];
		else throw new Exception("No locale specified.");
	}

}
