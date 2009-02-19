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
        return "It can be helpful to distinguish between <i>web-style</i> and"
                + " <i>application-style</i> layouting (although this is a"
                + " simplification). Both styles are supported, and can be used"
                + " simultaneously.<br/> Web-style layouting allows the content"
                + " to dictate the size of the components by \"pushing\" the"
                + " size, causing scrollbars to appear for the whole window"
                + " when needed. This can be achieved by not setting the size"
                + " for components, or setting an absolute size (e.g 200px)."
                + "<br/>Try resizing the window to see how the content reacts.";
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
