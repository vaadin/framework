package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class WebLayout extends Feature {

    @Override
    public String getName() {
        return "Web-style layout";
    }

    @Override
    public String getDescription() {
        return "Web-style layout refers to a layout style where the"
                + " content 'pushes' the layout to a reasonable size."
                + " In some cases you should also define some limits for"
                + " this 'pushing' so that the layout will stay reasonable."
                + "<br>Please click the button for a demo in a new window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class),
                new APIResource(VerticalLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ApplicationLayout.class, CustomLayouts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
