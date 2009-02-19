package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;

public class HorizontalLayoutBasic extends Feature {

    @Override
    public String getName() {
        return "Horizontal layout";
    }

    @Override
    public String getDescription() {
        return "The HorizontalLayout arranges components horizontally.<br/>It supports all basic features, plus some advanced stuff - including spacing, margin, alignment, and expand ratios.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { VerticalLayoutBasic.class, LayoutSpacing.class,
                LayoutAlignment.class, };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
