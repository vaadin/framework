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
        return "It can be helpful to distinguish between <i>web-style</i> and"
                + " <i>application-style</i> layouting (although this is a"
                + " simplification). Both styles are supported, and can be used"
                + " simultaneously.<br/> Application-style layouting uses relatively"
                + " -sized components, growing dynamically with the window, and"
                + " causing scrollbars to appear on well-defined areas within the"
                + " window."
                + "<br/>Try resizing the window to see how the content reacts.";
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
