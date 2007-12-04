/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

public class DateLocale extends
        com.itmill.toolkit.terminal.gwt.client.util.DateLocale {

    private static String locale;

    public DateLocale() {
        locale = LocaleService.getDefaultLocale();
    }

    public static void setLocale(String l) {
        if (LocaleService.getAvailableLocales().contains(locale)) {
            locale = l;
        } else {
            // TODO redirect to console
            System.out.println("Tried to use an unloaded locale \"" + locale
                    + "\". Using default in stead (" + locale + ")");
        }
    }

    public static String getAM() {
        try {
            return LocaleService.getAmPmStrings(locale)[0];
        } catch (final LocaleNotLoadedException e) {
            // TODO redirect to console
            System.out.println("Tried to use an unloaded locale \"" + locale
                    + "\".");
            return "AM";
        }
    }

    public static String getPM() {
        try {
            return LocaleService.getAmPmStrings(locale)[1];
        } catch (final LocaleNotLoadedException e) {
            // TODO redirect to console
            System.out.println("Tried to use an unloaded locale \"" + locale
                    + "\".");
            return "PM";
        }
    }

    public String[] getWEEKDAY_LONG() {
        try {
            return LocaleService.getDayNames(locale);
        } catch (final LocaleNotLoadedException e) {
            // TODO redirect to console
            System.out.println("Tried to use an unloaded locale \"" + locale
                    + "\".");
            return null;
        }
    }

    public String[] getWEEKDAY_SHORT() {
        try {
            return LocaleService.getShortDayNames(locale);
        } catch (final LocaleNotLoadedException e) {
            // TODO redirect to console
            System.out.println("Tried to use an unloaded locale \"" + locale
                    + "\".");
            return null;
        }
    }
}
