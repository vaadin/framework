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
        final String className = resolveWidgetTypeName(uidl);
        if ("com.itmill.toolkit.demo.coverflow.gwt.client.ui.ICoverflow"
                .equals(className)) {
            return new ICoverflow();
        }

        // Let the DefaultWidgetSet handle creation of default widgets
        return super.createWidget(uidl);
    }

    /** Resolves UIDL tag name to class name. */
    protected String resolveWidgetTypeName(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("cover".equals(tag)) {
            return "com.itmill.toolkit.demo.coverflow.gwt.client.ui.ICoverflow";
        }

        // Let the DefaultWidgetSet handle resolution of default widgets
        return super.resolveWidgetTypeName(uidl);
    }
}