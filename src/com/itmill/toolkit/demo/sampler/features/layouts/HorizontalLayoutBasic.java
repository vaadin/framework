package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class HorizontalLayoutBasic extends Feature {

    @Override
    public String getName() {
        return "Horizontal Layout";
    }

    @Override
    public String getDescription() {
        return "Horizontal and vertical layouts are the foremost " +
        		"basic layouts of all applications. You can add spacing " +
        		"between the elements, set the alignment of the components " +
        		"inside the cells of the layouts, and set one or more " +
        		"components as expanding so that they fill the available space " +
        		"according to the specified expand ratio.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] {
                new APIResource(HorizontalLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {
                VerticalLayoutBasic.class,
                LayoutSpacing.class,
                LayoutAlignment.class,
                };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
                new NamedExternalResource("Reference Manual: HorizontalLayout",
                        "/doc/manual/layout.components.orderedlayout.html"),
                        };
    }
}
