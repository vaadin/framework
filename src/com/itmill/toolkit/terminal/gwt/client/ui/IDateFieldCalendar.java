/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IDateFieldCalendar extends IDateField {

    private final CalendarPanel date;

    public IDateFieldCalendar() {
        super();
        date = new CalendarPanel(this);
        add(date);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        date.updateCalendar();
    }

}
