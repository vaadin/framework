package com.vaadin.demo.sampler.features.windows;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SubwindowModal extends Feature {

    @Override
    public String getName() {
        return "Modal window";
    }

    @Override
    public String getDescription() {
        return "A <i>modal window</i> blocks access to the rest of the application"
                + " until the window is closed (or made non-modal).<br/>"
                + " Use modal windows when the user must finish the task in the"
                + " window before continuing.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] {
        //
                Subwindow.class, //
                FeatureSet.Windows.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
        //
        new NamedExternalResource("Wikipedia: Modal window",
                "http://en.wikipedia.org/wiki/Modal_window"), //

        };
    }

}
