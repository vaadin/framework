package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class WindowNativeNew extends Feature {

    @Override
    public String getDescription() {
        return "Creates and opens a new native browser window with its own state."
                + "<br>Native browser"
                + " windows can either share the same state (instance) or have their own"
                + " internal state (instance). In practice the former option means that the"
                + " URL of the window will always be same (pointing to the same Window object"
                + ", whereas using the latter option generates a new URL (and Window instance)"
                + " for each new window."
                + "<br>It is essential to remember to remove the closed windows from the"
                + " application e.g. by implementing the Window.CloseListener interface.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WindowNativeShared.class, WindowChild.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
