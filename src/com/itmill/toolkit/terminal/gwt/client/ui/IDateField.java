/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.DateTimeService;
import com.itmill.toolkit.terminal.gwt.client.LocaleNotLoadedException;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IDateField extends FlowPanel implements Paintable {

    public static final String CLASSNAME = "i-datefield";

    protected String id;

    protected ApplicationConnection client;

    protected boolean immediate;

    public static int RESOLUTION_YEAR = 0;
    public static int RESOLUTION_MONTH = 1;
    public static int RESOLUTION_DAY = 2;
    public static int RESOLUTION_HOUR = 3;
    public static int RESOLUTION_MIN = 4;
    public static int RESOLUTION_SEC = 5;
    public static int RESOLUTION_MSEC = 6;

    protected int currentResolution = RESOLUTION_YEAR;

    protected String currentLocale;

    protected boolean readonly;

    protected boolean enabled;

    protected Date date = null;

    protected DateTimeService dts;

    public IDateField() {
        setStyleName(CLASSNAME);
        dts = new DateTimeService();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let layout manage caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save details
        this.client = client;
        id = uidl.getId();
        immediate = uidl.getBooleanAttribute("immediate");

        readonly = uidl.getBooleanAttribute("readonly");
        enabled = !uidl.getBooleanAttribute("disabled");

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                dts.setLocale(locale);
                currentLocale = locale;
            } catch (final LocaleNotLoadedException e) {
                currentLocale = dts.getLocale();
                // TODO redirect this to console
                System.out.println("Tried to use an unloaded locale \""
                        + locale + "\". Using default locale (" + currentLocale
                        + ").");
            }
        }

        int newResolution;
        if (uidl.hasVariable("msec")) {
            newResolution = RESOLUTION_MSEC;
        } else if (uidl.hasVariable("sec")) {
            newResolution = RESOLUTION_SEC;
        } else if (uidl.hasVariable("min")) {
            newResolution = RESOLUTION_MIN;
        } else if (uidl.hasVariable("hour")) {
            newResolution = RESOLUTION_HOUR;
        } else if (uidl.hasVariable("day")) {
            newResolution = RESOLUTION_DAY;
        } else if (uidl.hasVariable("month")) {
            newResolution = RESOLUTION_MONTH;
        } else {
            newResolution = RESOLUTION_YEAR;
        }

        currentResolution = newResolution;

        final int year = uidl.getIntVariable("year");
        final int month = (currentResolution >= RESOLUTION_MONTH) ? uidl
                .getIntVariable("month") : -1;
        final int day = (currentResolution >= RESOLUTION_DAY) ? uidl
                .getIntVariable("day") : -1;
        final int hour = (currentResolution >= RESOLUTION_HOUR) ? uidl
                .getIntVariable("hour") : -1;
        final int min = (currentResolution >= RESOLUTION_MIN) ? uidl
                .getIntVariable("min") : -1;
        final int sec = (currentResolution >= RESOLUTION_SEC) ? uidl
                .getIntVariable("sec") : -1;
        final int msec = (currentResolution >= RESOLUTION_MSEC) ? uidl
                .getIntVariable("msec") : -1;

        // Construct new date for this datefield (only if not null)
        if (year > -1) {
            date = new Date((long) getTime(year, month, day, hour, min, sec,
                    msec));
        } else {
            date = null;
        }

    }

    /*
     * We need this redundant native function because Java's Date object doesn't
     * have a setMilliseconds method.
     */
    private static native double getTime(int y, int m, int d, int h, int mi,
            int s, int ms)
    /*-{
       try {
       	var date = new Date();
       	if(y && y >= 0) date.setFullYear(y);
       	if(m && m >= 1) date.setMonth(m-1);
       	if(d && d >= 0) date.setDate(d);
       	if(h && h >= 0) date.setHours(h);
       	if(mi && mi >= 0) date.setMinutes(mi);
       	if(s && s >= 0) date.setSeconds(s);
       	if(ms && ms >= 0) date.setMilliseconds(ms);
       	return date.getTime();
       } catch (e) {
       	// TODO print some error message on the console
       	//console.log(e);
       	return (new Date()).getTime();
       }
    }-*/;

    public int getMilliseconds() {
        return (int) (date.getTime() - date.getTime() / 1000 * 1000);
    }

    public void setMilliseconds(int ms) {
        date.setTime(date.getTime() / 1000 * 1000 + ms);
    }

    public int getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(int currentResolution) {
        this.currentResolution = currentResolution;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Date getCurrentDate() {
        return date;
    }

    public void setCurrentDate(Date date) {
        this.date = date;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DateTimeService getDateTimeService() {
        return dts;
    }

    public String getId() {
        return id;
    }

    public ApplicationConnection getClient() {
        return client;
    }
}
