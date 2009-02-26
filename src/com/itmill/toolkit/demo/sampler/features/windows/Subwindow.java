package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class Subwindow extends Feature {

    @Override
    public String getName() {
        return "Subwindow";
    }

    @Override
    public String getDescription() {
        return "A <i>Subwindow</i> is a popup-window within the browser window."
                + " There can be multiple subwindows in one (native) browser"
                + " window.";
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
