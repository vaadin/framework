package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Window;

public class WindowChildAutosize extends Feature {

    @Override
    public String getName() {
        return "Window autosizing";
    }

    @Override
    public String getDescription() {
        return "Creates and opens a new floating child window with its own state."
                + "<br>The size of this child window will be adjusted automatically"
                + " to fit the content (in this example, the caption). This is default"
                + " behavior for a child window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowNativeNew.class,
                WindowChild.class, WindowChildModal.class,
                WindowChildPositionSize.class, WindowChildScrollable.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Component getExample() {
        return new WindowChildExample();
    }

}
