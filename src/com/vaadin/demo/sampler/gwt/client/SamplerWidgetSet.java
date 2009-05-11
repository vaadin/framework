package com.vaadin.demo.sampler.gwt.client;

import com.vaadin.demo.sampler.gwt.client.ui.VActiveLink;
import com.vaadin.demo.sampler.gwt.client.ui.VCodeLabel;
import com.vaadin.demo.sampler.gwt.client.ui.VGoogleAnalytics;
import com.vaadin.terminal.gwt.client.DefaultWidgetSet;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class SamplerWidgetSet extends DefaultWidgetSet {

    @Override
    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (VGoogleAnalytics.class == classType) {
            return new VGoogleAnalytics();
        } else if (VCodeLabel.class == classType) {
            return new VCodeLabel();
        } else if (VActiveLink.class == classType) {
            return new VActiveLink();
        } else {
            return super.createWidget(uidl);
        }
    }

    @Override
    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("googleanalytics".equals(tag)) {
            return VGoogleAnalytics.class;
        } else if ("codelabel".equals(tag)) {
            return VCodeLabel.class;
        } else if ("activelink".equals(tag)) {
            return VActiveLink.class;
        } else {
            return super.resolveWidgetType(uidl);
        }
    }

}
