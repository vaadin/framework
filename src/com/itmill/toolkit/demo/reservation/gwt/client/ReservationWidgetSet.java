/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.reservation.gwt.client;

import com.itmill.toolkit.demo.reservation.gwt.client.ui.ICalendarField;
import com.itmill.toolkit.demo.reservation.gwt.client.ui.IGoogleMap;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ReservationWidgetSet extends DefaultWidgetSet {
    public Paintable createWidget(UIDL uidl) {
        final Class type = resolveWidgetType(uidl);
        if (IGoogleMap.class == type) {
            return new IGoogleMap();
        } else if (ICalendarField.class == type) {
            return new ICalendarField();
        }

        return super.createWidget(uidl);
    }

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
