package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class LayoutSpacing extends Feature {

    @Override
    public String getName() {
        return "Layout Spacing";
    }

    @Override
    public String getDescription() {
        return "Layouts do not have spacing between the cells by default. " +
        		"The setSpacing() method turns spacing on. You can " +
        		"customize the amount of spacing in a theme.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] {
                new APIResource(VerticalLayout.class),
                new APIResource(HorizontalLayout.class),
                new APIResource(GridLayout.class),
                };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {
                HorizontalLayoutBasic.class,
                VerticalLayoutBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
                new NamedExternalResource("Reference Manual: Spacing",
                        "/doc/manual/layout.settings.spacing.html"),
                        };
    }
}
