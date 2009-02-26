package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class SubwindowSized extends Feature {

    @Override
    public String getName() {
        return "Window, explicit size";
    }

    @Override
    public String getDescription() {
        return "The size of a window can be specified - here the width is set"
                + " in pixels, and the height in percent.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { SubwindowModal.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
