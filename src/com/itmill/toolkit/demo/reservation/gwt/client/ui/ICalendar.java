package com.itmill.toolkit.demo.reservation.gwt.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.DateTimeService;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.CalendarPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IDateField;
import com.itmill.toolkit.terminal.gwt.client.ui.CalendarPanel.CalendarEntrySource;

public class ICalendar extends IDateField {

	private CalendarPanel calPanel;

	private SimplePanel hourPanel;

	private FlexTable hourTable;

	private EntrySource entrySource;

	private TableListener ftListener = new HourTableListener();

	private int realResolution = RESOLUTION_DAY;

	private static final String CLASSNAME = IDateField.CLASSNAME
			+ "-entrycalendar";

	public ICalendar() {
		super();
		setStyleName(CLASSNAME);
		calPanel = new CalendarPanel(this);
		add(calPanel);
		this.entrySource = new EntrySource();
		calPanel.setCalendarEntrySource(this.entrySource);
		calPanel.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, int row,
					int cell) {
				buildDayView(date);
			}
		});
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);
		// We want to draw our own hour list
		this.realResolution = currentResolution;
		this.currentResolution = RESOLUTION_DAY;
		if (uidl.hasAttribute("min")) {
			String mins = uidl.getStringAttribute("min");
			long min = (mins != null ? Long.parseLong(mins) : 0);
			String maxs = uidl.getStringAttribute("max");
			long max = (maxs != null ? Long.parseLong(maxs) : 0);
			Date minDate = (min > 0 ? new Date(min) : null);
			Date maxDate = (max > 0 ? new Date(max) : null);
			calPanel.setLimits(minDate, maxDate);
		}
		this.entrySource.clear();
		for (Iterator cit = uidl.getChildIterator(); cit.hasNext();) {
			UIDL child = (UIDL) cit.next();
			if (child.getTag().equals("items")) {
				for (Iterator iit = child.getChildIterator(); iit.hasNext();) {
					UIDL item = (UIDL) iit.next();
					this.entrySource.addItem(item);
				}
				break;
			}
		}
		calPanel.updateCalendar();
		buildDayView(this.date);
	}

	protected void buildDayView(Date date) {
		boolean firstRender = true;
		if (this.hourPanel == null) {
			this.hourPanel = new SimplePanel();
			this.hourPanel.setStyleName(CLASSNAME + "-hours");
			this.calPanel.getFlexCellFormatter().setColSpan(8, 0, 7);
			this.calPanel.setWidget(8, 0, this.hourPanel);
		} else {
			firstRender = false;
			this.hourPanel.clear();
		}
		this.hourTable = new FlexTable();
		this.hourTable.addTableListener(this.ftListener);
		this.hourPanel.add(this.hourTable);
		this.hourTable.setCellSpacing(1);

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
			if (this.dts.isTwelveHourClock()) {
				String ampm = (i < 12 ? "am" : "pm");
				hstr = (i <= 12 ? i : i - 12) + ":00 " + ampm;
			}
			hourTable.setHTML(i, 0, "<span>" + hstr + "</span>");
			hourTable.getCellFormatter()
					.setStyleName(i, 0, CLASSNAME + "-time");
		}

		List entries = this.entrySource.getEntries(date,
				DateTimeService.RESOLUTION_DAY);
		int currentCol = 1;
		for (Iterator it = entries.iterator(); it.hasNext();) {
			CalendarEntry entry = (CalendarEntry) it.next();
			int start = 0;
			int hours = 24;
			if (!entry.isNotime()) {
				Date d = entry.getStart();
				// TODO consider month&year as well
				start = (d.getDate() < date.getDate() ? 0 : d.getHours());
				d = entry.getEnd();
				hours = (d.getDate() > date.getDate() ? 24 : d.getHours())
						- start;
				if (hours == 0) {
					// We can't draw entries smaller than one
					hours = 1;
				}
			}
			int col = currentCol;
			if (col > 1) {
				while (!this.hourTable.isCellPresent(start, col - 1))
					col--;
			}
			this.hourTable.setHTML(start, col, "<span>"
					+ (entry.getTitle() != null ? entry.getTitle() : "&nbsp")
					+ "</span>");
			this.hourTable.getFlexCellFormatter().setRowSpan(start, col, hours);
			this.hourTable.getFlexCellFormatter().setStyleName(start, col,
					CLASSNAME + "-entry");
			String sn = entry.getStyleName();
			if (sn != null && !sn.equals("")) {
				this.hourTable.getFlexCellFormatter().addStyleName(start, col,
						CLASSNAME + "-" + entry.getStyleName());
			}
			Element el = this.hourTable.getFlexCellFormatter().getElement(
					start, col);

			String tooltip;
			if (DateTimeService.isSameDay(entry.getStart(), entry.getEnd())) {
				tooltip = (start < 10 ? "0" : "") + start + ":00";
				if (this.dts.isTwelveHourClock()) {
					String ampm = (start < 12 ? "am" : "pm");
					tooltip = (start <= 12 ? start : start - 12) + ":00 "
							+ ampm;

				}
				tooltip += " (" + hours + "h) ";
				if (entry.getTitle()!=null) {
					tooltip += entry.getTitle() + "\n ";
				}
			} else {
				tooltip = entry.getStringForDate(entry.getEnd()) + "\n ";
			}
			if (entry.getDescription()!=null) {
				tooltip += "\"" + entry.getDescription() + "\"";
			}
			DOM.setElementProperty(el, "title", tooltip);

			currentCol++;
		}

		// int hour = new Date().getHours()+1; // scroll to current hour
		int hour = this.date.getHours() + 1; // scroll to selected hour
		int h1 = (int) this.hourPanel.getOffsetHeight() / 2;
		int oh = this.hourTable.getOffsetHeight();
		int h2 = (int) (hour / 24.0 * oh);
		int scrollTop = (int) h2 - h1;
		Element el = this.hourPanel.getElement();
		setScrollTop(el, scrollTop);

	}

	private native void setScrollTop(Element el, int scrollTop) /*-{
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

	private class EntrySource implements CalendarPanel.CalendarEntrySource {

		private HashMap dates = new HashMap();

		public void addItem(UIDL item) {
			String styleName = item.getStringAttribute("styleName");
			Integer id = new Integer(item.getIntAttribute("id"));
			long start = Long.parseLong(item.getStringAttribute("start"));
			Date startDate = new Date(start);
			long end = -1;
			try {
				end = Long.parseLong(item.getStringAttribute("end"));
			} catch (Exception IGNORED) {
				// IGNORED attribute not required
			}
			Date endDate = (end > 0 && end != start ? new Date(end) : new Date(
					start));
			String title = item.getStringAttribute("title");
			String desc = item.getStringAttribute("description");
			boolean notime = item.getBooleanAttribute("notime");
			CalendarEntry entry = new CalendarEntry(styleName, startDate,
					endDate, title, desc, notime);

			// TODO should remove+readd if the same entry (id) is added again

			for (Date d = entry.getStart(); d.getYear() <= entry.getEnd()
					.getYear()
					&& d.getMonth() <= entry.getEnd().getYear()
					&& d.getDate() <= entry.getEnd().getDate(); d.setTime(d
					.getTime() + 86400000)) {
				String key = d.getYear() + "" + d.getMonth() + "" + d.getDate();
				ArrayList l = (ArrayList) dates.get(key);
				if (l == null) {
					l = new ArrayList();
					dates.put(key, l);
				}
				l.add(entry);
			}
		}

		public List getEntries(Date date, int resolution) {
			List entries = (List) dates.get(date.getYear() + ""
					+ date.getMonth() + "" + date.getDate());
			ArrayList res = new ArrayList();
			if (entries == null) {
				return res;
			}
			for (Iterator it = entries.iterator(); it.hasNext();) {
				CalendarEntry item = (CalendarEntry) it.next();
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
