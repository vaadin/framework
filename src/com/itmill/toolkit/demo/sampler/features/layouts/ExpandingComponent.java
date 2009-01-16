package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;

public class ExpandingComponent extends Feature {

    @Override
    public String getName() {
        return "Expanding components";
    }

    @Override
    public String getDescription() {
        return "With the setExpandRatio() -method you can set varying expand"
                + " ratios for the components inside a Layout.<br>Click the button to"
                + " open an example in a new window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class),

        };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {};
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
