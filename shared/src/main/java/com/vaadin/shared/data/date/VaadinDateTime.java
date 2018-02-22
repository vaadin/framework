package com.vaadin.shared.data.date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import com.vaadin.shared.ui.datefield.DateTimeResolution;

public class VaadinDateTime {
    public final int year;
    public final int month;
    public final int day;
    public final int hour;
    public final int minute;
    public final int sec;
    public final DateTimeResolution resolution;

    public VaadinDateTime(VaadinDateTime date, int hour, int minute, int second) {
        this(date.getYear(), date.getMonth(),date.getDay(),hour, minute, second);
    }

    public VaadinDateTime(int year, int month, int day, int hour, int minute, int sec) {
        this(year, month, day, hour,minute,sec,DateTimeResolution.SECOND);
    }

    public VaadinDateTime(int year, int month, int day) {
        this(year, month, day, 0,0,0,DateTimeResolution.DAY);
    }

    public VaadinDateTime(int year, int month, int day, int hour, int minute, int sec,
                          DateTimeResolution resolution) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.sec = sec;
        this.resolution = resolution;

    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSec() {
        return sec;
    }

    public DateTimeResolution getResolution() {
        return resolution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VaadinDateTime that = (VaadinDateTime) o;
        return year == that.year &&
                month == that.month &&
                day == that.day &&
                hour == that.hour &&
                minute == that.minute &&
                sec == that.sec &&
                resolution == that.resolution;
    }

    @Override
    public int hashCode() {

        return Objects.hash(year, month, day, hour, minute, sec, resolution);
    }

    public static VaadinDateTime now() {
        GregorianCalendar calendar = new GregorianCalendar();
        return new VaadinDateTime(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
                );
    }

    public static VaadinDateTime today() {
        GregorianCalendar calendar = new GregorianCalendar();
        return new VaadinDateTime(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
        );
    }

    public boolean after(VaadinDateTime rangeEnd) {
        for(DateTimeResolution r = DateTimeResolution.YEAR; r.ordinal()<=resolution.ordinal();r = DateTimeResolution.values()[r.ordinal()-1])
        {
            if(getField(r) > rangeEnd.getField(r)) return true;
        }
        return false;
    }

    public int getField(DateTimeResolution r) {
        switch (r) {
            case YEAR: return getYear();
            case MONTH: return getMonth();
            case DAY: return getDay();
            case HOUR: return getHour();
            case MINUTE: return getMinute();
            case SECOND: return getSec();
        }
        return 0;
    }
}
