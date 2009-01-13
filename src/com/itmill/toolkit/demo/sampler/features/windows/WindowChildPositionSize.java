package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class WindowChildPositionSize extends Feature {

    @Override
    public String getName() {
        return "Window position and size";
    }

    @Override
    public String getDescription() {
        return "Creates and opens a new floating child window with specified"
                + " size and position attributes. This child window is also"
                + " set to allow resizing.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowNativeNew.class,
                WindowChild.class, WindowChildAutosize.class,
                WindowChildModal.class, WindowChildScrollable.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
