package com.itmill.toolkit.demo.reservation.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.demo.reservation.gwt.client.ui.ICalendarField;
import com.itmill.toolkit.demo.reservation.gwt.client.ui.IGoogleMap;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ReservationWidgetSet extends DefaultWidgetSet {
	public Widget createWidget(UIDL uidl) {
		String className = resolveWidgetTypeName(uidl);
		if ("com.itmill.toolkit.terminal.gwt.client.ui.IGoogleMap"
				.equals(className)) {
			return new IGoogleMap();
		} else if ("com.itmill.toolkit.demo.reservation.gwt.client.ui.ICalendarField"
				.equals(className)) {
			return new ICalendarField();
		}

		return super.createWidget(uidl);
	}

	protected String resolveWidgetTypeName(UIDL uidl) {

		String tag = uidl.getTag();
		if ("googlemap".equals(tag)) {
			return "com.itmill.toolkit.terminal.gwt.client.ui.IGoogleMap";
		} else if ("calendarfield".equals(tag)) {
			return "com.itmill.toolkit.demo.reservation.gwt.client.ui.ICalendarField";
		}

		return super.resolveWidgetTypeName(uidl);
	}

	public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {
		return GWT.getTypeName(currentWidget).equals(
				resolveWidgetTypeName(uidl));
	}
}
