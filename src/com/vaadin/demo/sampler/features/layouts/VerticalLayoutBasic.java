package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutBasic extends Feature {

    @Override
    public String getName() {
        return "Vertical layout";
    }

    @Override
    public String getDescription() {
        return "The VerticalLayout arranges components vertically. "
                + " It is 100% wide by default, which is nice in many cases,"
                + " but something to be aware of if trouble arises.<br/>It supports all basic features, plus some advanced stuff - including spacing, margin, alignment, and expand ratios.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(VerticalLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { HorizontalLayoutBasic.class, LayoutSpacing.class,
                LayoutAlignment.class, };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
