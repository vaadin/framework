package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

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

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { SubwindowSized.class, FeatureSet.Windows.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
