package com.itmill.toolkit.demo.sampler.gwt.client;

import com.itmill.toolkit.demo.sampler.gwt.client.ui.IActiveLink;
import com.itmill.toolkit.demo.sampler.gwt.client.ui.ICodeLabel;
import com.itmill.toolkit.demo.sampler.gwt.client.ui.IGoogleAnalytics;
import com.itmill.toolkit.terminal.gwt.client.DefaultWidgetSet;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class SamplerWidgetSet extends DefaultWidgetSet {

    @Override
    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (IGoogleAnalytics.class == classType) {
            return new IGoogleAnalytics();
        } else if (ICodeLabel.class == classType) {
            return new ICodeLabel();
        } else if (IActiveLink.class == classType) {
            return new IActiveLink();
        } else {
            return super.createWidget(uidl);
        }
    }

    @Override
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("googleanalytics".equals(tag)) {
            return IGoogleAnalytics.class;
        } else if ("codelabel".equals(tag)) {
            return ICodeLabel.class;
        } else if ("activelink".equals(tag)) {
            return IActiveLink.class;
        } else {
            return super.resolveWidgetType(uidl);
        }
    }

}
