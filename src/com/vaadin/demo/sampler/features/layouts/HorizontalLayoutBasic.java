package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { VerticalLayoutBasic.class, LayoutSpacing.class,
                LayoutAlignment.class, };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
