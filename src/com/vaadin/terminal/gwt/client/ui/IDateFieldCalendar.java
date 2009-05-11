/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class IDateFieldCalendar extends IDateField {

    private final ICalendarPanel date;

    public IDateFieldCalendar() {
        super();
        date = new ICalendarPanel(this);
        add(date);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        date.updateCalendar();
    }

}
