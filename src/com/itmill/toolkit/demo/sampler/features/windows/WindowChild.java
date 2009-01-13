package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class WindowChild extends Feature {

    @Override
    public String getDescription() {
        return "Creates and opens a new floating child window with its own state."
                + "<br>Child windows are typically used for Dialog Windows and"
                + " Multiple Document Interface applications.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowNativeNew.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
