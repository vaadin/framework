/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation.gwt.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.CalendarEntry;
import com.vaadin.terminal.gwt.client.ui.ICalendarPanel;
import com.vaadin.terminal.gwt.client.ui.IDateField;

public class ICalendarField extends IDateField {

    private final ICalendarPanel calPanel;

    private SimplePanel hourPanel;

    private FlexTable hourTable;

    private final EntrySource entrySource;

    private final TableListener ftListener = new HourTableListener();

    private int realResolution = RESOLUTION_DAY;

    private static final String CLASSNAME = IDateField.CLASSNAME
            + "-entrycalendar";

    public ICalendarField() {
        super();
        setStyleName(CLASSNAME);
        calPanel = new ICalendarPanel(this);
        add(calPanel);
        entrySource = new EntrySource();
        calPanel.setCalendarEntrySource(entrySource);
        calPanel.addTableListener(new TableListener() {
            public void onCellClicked(SourcesTableEvents sender, int row,
                    int cell) {
                buildDayView(date);
            }
        });
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        // We want to draw our own hour list
        realResolution = currentResolution;
        currentResolution = RESOLUTION_DAY;
        if (uidl.hasAttribute("min")) {
            final String mins = uidl.getStringAttribute("min");
            final long min = (mins != null ? Long.parseLong(mins) : 0);
            final String maxs = uidl.getStringAttribute("max");
            final long max = (maxs != null ? Long.parseLong(maxs) : 0);
            final Date minDate = (min > 0 ? new Date(min) : null);
            final Date maxDate = (max > 0 ? new Date(max) : null);
            calPanel.setLimits(minDate, maxDate);
        }
        entrySource.clear();
        for (final Iterator cit = uidl.getChildIterator(); cit.hasNext();) {
            final UIDL child = (UIDL) cit.next();
            if (child.getTag().equals("items")) {
                for (final Iterator iit = child.getChildIterator(); iit
                        .hasNext();) {
                    final UIDL item = (UIDL) iit.next();
                    entrySource.addItem(item);
                }
                break;
            }
        }
        calPanel.updateCalendar();
        buildDayView(date);
    }

    protected void buildDayView(Date date) {
        if (hourPanel == null) {
            hourPanel = new SimplePanel();
            hourPanel.setStyleName(CLASSNAME + "-hours");
            calPanel.getFlexCellFormatter().setColSpan(8, 0, 7);
            calPanel.setWidget(8, 0, hourPanel);
        } else {
            hourPanel.clear();
        }
        hourTable = new FlexTable();
        hourTable.addTableListener(ftListener);
        hourPanel.add(hourTable);
        hourTable.setCellSpacing(1);

        for (int i = 0; i < 24; i++) {
            String style = (i % 2 == 0 ? "even" : "odd");
            if (realResolution >= RESOLUTION_HOUR) {
                if (this.date != null && this.date.getHours() == i) {
                    style = "selected";
                }
            }
            hourTable.getRowFormatter().setStyleName(i,
                    CLASSNAME + "-row-" + style);
            String hstr = (i < 10 ? "0" : "") + i + ":00";
            if (dts.isTwelveHourClock()) {
                final String ampm = (i < 12 ? "am" : "pm");
                hstr = (i <= 12 ? i : i - 12) + ":00 " + ampm;
            }
            hourTable.setHTML(i, 0, "<span>" + hstr + "</span>");
            hourTable.getCellFormatter()
                    .setStyleName(i, 0, CLASSNAME + "-time");
        }

        final List entries = entrySource.getEntries(date,
                DateTimeService.RESOLUTION_DAY);
        int currentCol = 1;
        for (final Iterator it = entries.iterator(); it.hasNext();) {
            final CalendarEntry entry = (CalendarEntry) it.next();
            int start = 0;
            int hours = 24;
            if (!entry.isNotime()) {
                Date d = entry.getStart();
                // TODO consider month&year as well
                start = (d.getDate() < date.getDate() ? 0 : d.getHours());
                d = entry.getEnd();
                hours = (d.getDate() > date.getDate() ? 24 : d.getHours())
                        - start;
                if (hours < 1) {
                    // We can't draw entries smaller than
                    // one
                    hours = 1;
                }
            }
            int col = currentCol;
            if (col > 1) {
                while (!hourTable.isCellPresent(start, col - 1)) {
                    col--;
                }
            }
            hourTable.setHTML(start, col, "<span>"
                    + (entry.getTitle() != null ? entry.getTitle() : "&nbsp")
                    + "</span>");
            hourTable.getFlexCellFormatter().setRowSpan(start, col, hours);
            hourTable.getFlexCellFormatter().setStyleName(start, col,
                    CLASSNAME + "-entry");
            final String sn = entry.getStyleName();
            if (sn != null && !sn.equals("")) {
                hourTable.getFlexCellFormatter().addStyleName(start, col,
                        CLASSNAME + "-" + entry.getStyleName());
            }
            final Element el = hourTable.getFlexCellFormatter().getElement(
                    start, col);

            String tooltip;
            if (DateTimeService.isSameDay(entry.getStart(), entry.getEnd())) {
                tooltip = (start < 10 ? "0" : "") + start + ":00";
                if (dts.isTwelveHourClock()) {
                    final String ampm = (start < 12 ? "am" : "pm");
                    tooltip = (start <= 12 ? start : start - 12) + ":00 "
                            + ampm;

                }
                tooltip += " (" + hours + "h) ";
                if (entry.getTitle() != null) {
                    tooltip += entry.getTitle() + "\n ";
                }
            } else {
                tooltip = entry.getStringForDate(entry.getEnd()) + "\n ";
            }
            if (entry.getDescription() != null) {
                tooltip += "\"" + entry.getDescription() + "\"";
            }
            DOM.setElementProperty(el, "title", tooltip);

            currentCol++;
        }

        // int hour = new Date().getHours()+1; // scroll to current hour
        Date d = (this.date != null ? this.date : new Date());
        final int hour = d.getHours() + 1; // scroll to selected
        // hour
        final int h1 = hourPanel.getOffsetHeight() / 2;
        final int oh = hourTable.getOffsetHeight();
        final int h2 = (int) (hour / 24.0 * oh);
        final int scrollTop = h2 - h1;
        final Element el = hourPanel.getElement();
        setScrollTop(el, scrollTop);

    }

    private native void setScrollTop(Element el, int scrollTop)
    /*-{
         el.scrollTop = scrollTop;
    }-*/;

    private class HourTableListener implements TableListener {

        public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
            if (realResolution < RESOLUTION_HOUR || date == null) {
                return;
            }
            date.setHours(row);
            client.updateVariable(id, "hour", row, immediate);
        }

    }

    private class EntrySource implements ICalendarPanel.CalendarEntrySource {

        private final HashMap dates = new HashMap();

        public void addItem(UIDL item) {
            final String styleName = item.getStringAttribute("styleName");
            // final Integer id = new Integer(item.getIntAttribute("id"));

            DateTimeFormat dtf = DateTimeFormat
                    .getFormat("d MM yyyy HH:mm:ss Z");

            Date startDate = dtf.parse(item.getStringAttribute("start"));

            // fix times with server-client difference
            int diff = (startDate.getTimezoneOffset() - item
                    .getIntAttribute("Z")) * 60000;
            startDate = new Date(startDate.getTime() + diff);
            Date endDate;
            if (item.hasAttribute("end")) {
                endDate = dtf.parse(item.getStringAttribute("end"));
                endDate = new Date(endDate.getTime() + diff);
            } else {
                endDate = (Date) startDate.clone();
            }
            final String title = item.getStringAttribute("title");
            final String desc = item.getStringAttribute("description");
            final boolean notime = item.getBooleanAttribute("notime");
            final CalendarEntry entry = new CalendarEntry(styleName, startDate,
                    endDate, title, desc, notime);

            // TODO should remove+readd if the same entry (id) is
            // added again

            for (final Date d = new Date(entry.getStart().getTime()); d
                    .getYear() <= entry.getEnd().getYear()
                    && d.getMonth() <= entry.getEnd().getYear()
                    && d.getDate() <= entry.getEnd().getDate(); d.setTime(d
                    .getTime() + 86400000)) {
                final String key = d.getYear() + "" + d.getMonth() + ""
                        + d.getDate();
                ArrayList l = (ArrayList) dates.get(key);
                if (l == null) {
                    l = new ArrayList();
                    dates.put(key, l);
                }
                l.add(entry);
            }
        }

        public List getEntries(Date date, int resolution) {
            final ArrayList res = new ArrayList();
            if (date == null) {
                return res;
            }
            final List entries = (List) dates.get(date.getYear() + ""
                    + date.getMonth() + "" + date.getDate());

            if (entries == null) {
                return res;
            }
            for (final Iterator it = entries.iterator(); it.hasNext();) {
                final CalendarEntry item = (CalendarEntry) it.next();
                if (DateTimeService.isInRange(date, item.getStart(), item
                        .getEnd(), resolution)) {
                    res.add(item);
                }
            }

            return res;
        }

        public void clear() {
            dates.clear();
        }

    }

}
