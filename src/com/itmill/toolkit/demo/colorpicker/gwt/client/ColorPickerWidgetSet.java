/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.colorpicker.gwt.client;

import com.itmill.toolkit.demo.colorpicker.gwt.client.ui.IColorPicker;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ColorPickerWidgetSet extends DefaultWidgetSet {
    /** Creates a widget according to its uidl. */
    public Paintable createWidget(UIDL uidl) {
        final Class type = resolveWidgetType(uidl);
        if (IColorPicker.class == type) {
            return new IColorPicker();
        }

        // Let the DefaultWidgetSet handle creation of default widgets
        return super.createWidget(uidl);
    }

    /** Resolves UIDL tag name to class. */
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("colorpicker".equals(tag)) {
            return IColorPicker.class;
        }

        // Let the DefaultWidgetSet handle resolution of default widgets
        return super.resolveWidgetType(uidl);
    }
}
