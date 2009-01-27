package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class ApplicationLayout extends Feature {

    @Override
    public String getName() {
        return "Application-style layout";
    }

    @Override
    public String getDescription() {
        return "Application-style layout refers to a layout style where the"
                + " Toolkit application is designed to look similar to a traditional"
                + " desktop application. Basically this means that the application"
                + " should usually fill the whole window with UI elements, and it"
                + " should also resize gracefully and resonably."
                + "<br>Please click the button for a demo in a new window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class),
                new APIResource(VerticalLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WebLayout.class, CustomLayouts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
