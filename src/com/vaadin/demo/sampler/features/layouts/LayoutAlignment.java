package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class LayoutAlignment extends Feature {

    @Override
    public String getName() {
        return "Component Alignment";
    }

    @Override
    public String getDescription() {
        return "GridLayout, VerticalLayout, and HorizontalLayout, "
                + "which are tabular layouts consisting of cells, "
                + "support alignment of components within the layout cells. "
                + "The alignment of a component within its respective cell "
                + "is set with setComponentAlignment().";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(VerticalLayout.class),
                new APIResource(HorizontalLayout.class),
                new APIResource(GridLayout.class), };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { HorizontalLayoutBasic.class,
                VerticalLayoutBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
