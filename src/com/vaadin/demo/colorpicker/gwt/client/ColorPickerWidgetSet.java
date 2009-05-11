/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.colorpicker.gwt.client;

import com.vaadin.demo.colorpicker.gwt.client.ui.IColorPicker;
import com.vaadin.terminal.gwt.client.DefaultWidgetSet;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class ColorPickerWidgetSet extends DefaultWidgetSet {
    /** Resolves UIDL tag name to widget class. */
    @Override
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("colorpicker".equals(tag)) {
            return IColorPicker.class;
        }

        // Let the DefaultWidgetSet handle resolution of default widgets
        return super.resolveWidgetType(uidl);
    }

    /** Creates a widget instance according to its class object. */
    @Override
    public Paintable createWidget(UIDL uidl) {
        final Class type = resolveWidgetType(uidl);
        if (IColorPicker.class == type) {
            return new IColorPicker();
        }

        // Let the DefaultWidgetSet handle creation of default widgets
        return super.createWidget(uidl);
    }
}
