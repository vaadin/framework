package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class LayoutSpacing extends Feature {

    @Override
    public String getName() {
        return "Layout Spacing";
    }

    @Override
    public String getDescription() {
        return "Spacing between components can be enabled or disabled."
                + " The actual size of the spacing is determined by the theme,"
                + " and can be customized with CSS.<br/>Note that <i>spacing</i>"
                + " is the space between components within the layout, and"
                + " <i>margin</i> is the space around the layout as a whole.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(VerticalLayout.class),
                new APIResource(HorizontalLayout.class),
                new APIResource(GridLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LayoutMargin.class, HorizontalLayoutBasic.class,
                VerticalLayoutBasic.class, GridLayoutBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
