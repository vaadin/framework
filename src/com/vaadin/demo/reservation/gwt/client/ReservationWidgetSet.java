/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation.gwt.client;

import com.vaadin.demo.reservation.gwt.client.ui.ICalendarField;
import com.vaadin.demo.reservation.gwt.client.ui.IGoogleMap;
import com.vaadin.terminal.gwt.client.DefaultWidgetSet;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class ReservationWidgetSet extends DefaultWidgetSet {
    @Override
    public Paintable createWidget(UIDL uidl) {
        final Class type = resolveWidgetType(uidl);
        if (IGoogleMap.class == type) {
            return new IGoogleMap();
        } else if (ICalendarField.class == type) {
            return new ICalendarField();
        }

        return super.createWidget(uidl);
    }

    @Override
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("googlemap".equals(tag)) {
            return IGoogleMap.class;
        } else if ("calendarfield".equals(tag)) {
            return ICalendarField.class;
        }
        return super.resolveWidgetType(uidl);
    }

}
