package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;

/**
 * Date / time etc. localisation service for all widgets.
 * Should cache all loaded locales as JSON strings.
 * 
 * @author Jouni Koivuviita
 *
 */
public class LocaleService {
	
	private Client client;
	
	private Map cache = new HashMap();
	
	public LocaleService(Client client){
		this.client = client;
	}
	
	private void loadLocale(String locale) {
		JSONObject resp = client.getLocale(locale);
		cache.put(locale, resp);
	}
	
	public String[] getMonthNames(String locale) {
		// TODO
		//if(cache.containsKey(locale))
		//else loadLocale(locale);
		String[] temp = new String[12];
		temp[0] = "tammi"; temp[1] = "helmi"; temp[2] = "maalis"; temp[3] = "huhti";
		temp[4] = "touko"; temp[5] = "kesä"; temp[6] = "heinä"; temp[7] = "elo";
		temp[8] = "syys"; temp[9] = "loka"; temp[10] = "marras"; temp[11] = "joulu";
		return temp;
	}
	
	public String[] getShortMonthNames(String locale) {
		// TODO
		//if(cache.containsKey(locale))
		//else loadLocale(locale);
		String[] temp = new String[12];
		temp[0] = "tam"; temp[1] = "hel"; temp[2] = "maa"; temp[3] = "huh";
		temp[4] = "tou"; temp[5] = "kes"; temp[6] = "hei"; temp[7] = "elo";
		temp[8] = "syy"; temp[9] = "lok"; temp[10] = "mar"; temp[11] = "jou";
		return temp;
	}
	
	public String[] getDayNames(String locale) {
		// TODO
		//if(cache.containsKey(locale))
		//else loadLocale(locale);
		String[] temp = new String[7];
		temp[1] = "maanatai"; temp[2] = "tiistai"; temp[3] = "keskiviikko";
		temp[4] = "torstai"; temp[5] = "perjantai"; temp[6] = "lauantai";
		temp[0] = "sunnuntai";
		return temp;
	}
	
	public String[] getShortDayNames(String locale) {
		// TODO
		//if(cache.containsKey(locale))
		//else loadLocale(locale);
		String[] temp = new String[7];
		temp[1] = "ma"; temp[2] = "ti"; temp[3] = "ke";
		temp[4] = "to"; temp[5] = "pe"; temp[6] = "la";
		temp[0] = "su";
		return temp;
	}
	
	public int getFirstDayOfWeek(String locale) {
		// TODO
		//if(cache.containsKey(locale))
		//else loadLocale(locale);
		return 1;
	}

}
