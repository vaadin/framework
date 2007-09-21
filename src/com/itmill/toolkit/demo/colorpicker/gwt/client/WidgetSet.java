package com.itmill.toolkit.demo.colorpicker.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.demo.colorpicker.gwt.client.ui.ClientColorPicker;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class WidgetSet extends DefaultWidgetSet {
    public Widget createWidget(UIDL uidl) {
    	String className = resolveWidgetTypeName(uidl);
    	if ("com.itmill.toolkit.demo.colorpicker.gwt.client.ui.ClientColorPicker"
    		.equals(className))
    		return new ClientColorPicker();

    	return super.createWidget(uidl);
    }

    protected String resolveWidgetTypeName(UIDL uidl) {
    	String tag = uidl.getTag();
    	if ("colorpicker".equals(tag))
    		return "com.itmill.toolkit.demo.colorpicker.gwt.client.ui.ClientColorPicker";

    	return super.resolveWidgetTypeName(uidl);
    }
}
