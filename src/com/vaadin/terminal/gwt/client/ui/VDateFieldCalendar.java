/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VDateFieldCalendar extends VDateField {

    private final VCalendarPanel date;

    public VDateFieldCalendar() {
        super();
        date = new VCalendarPanel(this);
        add(date);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        date.updateCalendar();
    }

}
