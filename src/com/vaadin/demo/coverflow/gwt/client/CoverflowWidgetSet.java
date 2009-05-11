/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.coverflow.gwt.client;

import com.vaadin.demo.coverflow.gwt.client.ui.VCoverflow;
import com.vaadin.terminal.gwt.client.DefaultWidgetSet;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class CoverflowWidgetSet extends DefaultWidgetSet {
    /** Creates a widget according to its class name. */
    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (VCoverflow.class == classType) {
            return new VCoverflow();
        }

        // Let the DefaultWidgetSet handle creation of default widgets
        return super.createWidget(uidl);
    }

    /** Resolves UIDL tag name to class . */
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("cover".equals(tag)) {
            return VCoverflow.class;
        }

        // Let the DefaultWidgetSet handle resolution of default widgets
        return super.resolveWidgetType(uidl);
    }
}