package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class WindowChildScrollable extends Feature {

    @Override
    public String getName() {
        return "Window - Scrollable";
    }

    @Override
    public String getDescription() {
        return "Creates and opens a new floating child window with specified"
                + " size and position attributes. The content for this window"
                + " is too big to fit in the specified size. Therefore you can"
                + " use the scrollbars to view different part of the content.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowNativeNew.class,
                WindowChild.class, WindowChildAutosize.class,
                WindowChildModal.class, WindowChildPositionSize.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
