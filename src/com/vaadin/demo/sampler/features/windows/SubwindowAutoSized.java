package com.vaadin.demo.sampler.features.windows;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SubwindowAutoSized extends Feature {

    @Override
    public String getName() {
        return "Window, automatic size";
    }

    @Override
    public String getDescription() {
        return "The window will be automatically sized to fit the contents,"
                + " if the size of the window (and it's layout) is undefined.<br/>"
                + " Note that by default Window contains a VerticalLayout that"
                + " is 100% wide.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { SubwindowSized.class, FeatureSet.Windows.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
