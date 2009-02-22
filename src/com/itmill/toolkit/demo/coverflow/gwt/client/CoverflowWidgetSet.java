/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.coverflow.gwt.client;

import com.itmill.toolkit.demo.coverflow.gwt.client.ui.ICoverflow;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class CoverflowWidgetSet extends DefaultWidgetSet {
    /** Creates a widget according to its class name. */
    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (ICoverflow.class == classType) {
            return new ICoverflow();
        }

        // Let the DefaultWidgetSet handle creation of default widgets
        return super.createWidget(uidl);
    }

    /** Resolves UIDL tag name to class . */
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("cover".equals(tag)) {
            return ICoverflow.class;
        }

        // Let the DefaultWidgetSet handle resolution of default widgets
        return super.resolveWidgetType(uidl);
    }
}