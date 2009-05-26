/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation.gwt.client;

import com.vaadin.demo.reservation.gwt.client.ui.VCalendarField;
import com.vaadin.demo.reservation.gwt.client.ui.VGoogleMap;
import com.vaadin.terminal.gwt.client.DefaultWidgetSet;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class ReservationWidgetSet extends DefaultWidgetSet {
    @Override
    public Paintable createWidget(UIDL uidl) {
        final Class<?> type = resolveWidgetType(uidl);
        if (VGoogleMap.class == type) {
            return new VGoogleMap();
        } else if (VCalendarField.class == type) {
            return new VCalendarField();
        }

        return super.createWidget(uidl);
    }

    @Override
    protected Class<?> resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("googlemap".equals(tag)) {
            return VGoogleMap.class;
        } else if ("calendarfield".equals(tag)) {
            return VCalendarField.class;
        }
        return super.resolveWidgetType(uidl);
    }

}
