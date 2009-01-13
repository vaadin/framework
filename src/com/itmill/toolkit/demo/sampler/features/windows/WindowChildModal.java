package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class WindowChildModal extends Feature {

    @Override
    public String getName() {
        return "Window modality";
    }

    @Override
    public String getDescription() {
        return "Creates and opens a new modal child window with its own state."
                + "<br>Child window modality means that you cannot access the"
                + " underlying window(s) while the modal window is displayed.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowNativeNew.class,
                WindowChild.class, WindowChildAutosize.class,
                WindowChildPositionSize.class, WindowChildScrollable.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
