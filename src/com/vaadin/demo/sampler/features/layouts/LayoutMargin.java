package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class LayoutMargin extends Feature {

    @Override
    public String getName() {
        return "Layout margin";
    }

    @Override
    public String getDescription() {
        return "Layouts can have margins on any of the sides. The actual size"
                + " of the margin is determined by the theme, and can be"
                + " customized using CSS - in this example, the right margin"
                + " size is increased.<br/>Note that <i>margin</i>"
                + " is the space around the layout as a whole, and"
                + " <i>spacing</i> is the space between the component within"
                + " the layout.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(VerticalLayout.class),
                new APIResource(HorizontalLayout.class),
                new APIResource(GridLayout.class), };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LayoutSpacing.class, HorizontalLayoutBasic.class,
                VerticalLayoutBasic.class, GridLayoutBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "CSS for the layout", getThemeBase()
                        + "layouts/marginexample.css") };
    }
}
