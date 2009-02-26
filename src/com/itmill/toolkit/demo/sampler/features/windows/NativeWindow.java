package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.FeatureSet.Links;
import com.itmill.toolkit.demo.sampler.FeatureSet.Windows;
import com.itmill.toolkit.ui.Window;

public class NativeWindow extends Feature {

    @Override
    public String getName() {
        return "Native window";
    }

    @Override
    public String getDescription() {
        return "A <i>NativeWindow</i> is a separate browser window, which"
                + " looks and works just like the main window.<br/>"
                + " There are multiple ways to make native windows; you can"
                + " override Application.getWindow() (recommended in any case)"
                + " but you can also use Application.addWindow() - the added"
                + " window will be available from a separate URL (which is"
                + " based on the window name.)<br/> When you view Sampler in"
                + " a new window, the getWindow() method is used, this example"
                + " also uses addWindow().";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { Subwindow.class, Links.class, Windows.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
