package com.itmill.toolkit.terminal.gwt.client.ui.calendar;

import java.util.Date;

import com.itmill.toolkit.terminal.gwt.client.DateTimeService;

public class ICalendarEntry {
    private Date start;
    private Date end;
    private String title;
    private boolean notime;

    public ICalendarEntry(Date start, Date end, String title, boolean notime) {
	if (notime) {
	    Date d = new Date(start.getTime());
	    d.setSeconds(0);
	    d.setMinutes(0);
	    this.start = d;
	    if (end != null) {
		d = new Date(end.getTime());
		d.setSeconds(0);
		d.setMinutes(0);
		this.end = d;
	    } else {
		end = start;
	    }
	} else {
	    this.start = start;
	    this.end = end;
	}
	this.title = title;
	this.notime = notime;
    }

    public ICalendarEntry(Date start, Date end, String title) {
	this(start, end, title, false);
    }

    public Date getStart() {
	return start;
    }

    public void setStart(Date start) {
	this.start = start;
    }

    public Date getEnd() {
	return end;
    }

    public void setEnd(Date end) {
	this.end = end;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public boolean isNotime() {
	return notime;
    }

    public void setNotime(boolean notime) {
	this.notime = notime;
    }

    public String getStringForDate(Date d) {
	// TODO format from DateTimeService
	String s = "";
	if (!notime) {
	    if (!DateTimeService.isSameDay(d, start)) {
		s += (start.getYear() + 1900) + "." + (start.getMonth() + 1)
			+ "." + start.getDate() + " ";
	    }
	    int i = start.getHours();
	    s += (i < 10 ? "0" : "") + i;
	    s += ":";
	    i = start.getMinutes();
	    s += (i < 10 ? "0" : "") + i;
	    if (!start.equals(end)) {
        	    s += " - ";
        	    if (!DateTimeService.isSameDay(start, end)) {
        		s += (end.getYear() + 1900) + "." + (end.getMonth() + 1) + "."
        			+ end.getDate() + " ";
        	    }
        	    i = end.getHours();
        	    s += (i < 10 ? "0" : "") + i;
        	    s += ":";
        	    i = end.getMinutes();
        	    s += (i < 10 ? "0" : "") + i;
	    }
	    s += " ";
	}
	s += title;
	return s;
    }

}