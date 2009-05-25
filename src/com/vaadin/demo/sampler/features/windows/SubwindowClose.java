package com.vaadin.demo.sampler.features.windows;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SubwindowClose extends Feature {

    @Override
    public String getName() {
        return "Window closing";
    }

    @Override
    public String getDescription() {
        return "Using a <i>CloseListener</i> one can detect when a window is closed.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { FeatureSet.Windows.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
