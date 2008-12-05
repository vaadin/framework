package com.itmill.toolkit.demo.sampler.gwt.client;

import com.itmill.toolkit.demo.sampler.gwt.client.ui.IWebAnalytics;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class SamplerWidgetSet extends DefaultWidgetSet {

    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (IWebAnalytics.class == classType) {
            return new IWebAnalytics();
        } else {
            return super.createWidget(uidl);
        }
    }

    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("webanalytics".equals(tag)) {
            return IWebAnalytics.class;
        } else {
            return super.resolveWidgetType(uidl);
        }
    }

}
