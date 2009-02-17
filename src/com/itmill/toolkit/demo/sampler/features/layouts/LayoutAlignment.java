package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

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
        return new NamedExternalResource[] { new NamedExternalResource(
                "Reference Manual: Layout Alignment",
                "/doc/manual/layout.features.alignment.html"), };
    }
}
