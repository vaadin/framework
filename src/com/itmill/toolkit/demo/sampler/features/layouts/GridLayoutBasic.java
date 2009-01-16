package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.GridLayout;

public class GridLayoutBasic extends Feature {

    @Override
    public String getName() {
        return "Grid Layout";
    }

    @Override
    public String getDescription() {
        return "GridLayout allow you to create a grid of components."
                + " The grid may have an arbitrary number of cells in each direction"
                + " and you can easily set components to fill multiple cells.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(GridLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { HorizontalLayoutBasic.class,
                VerticalLayoutBasic.class, LayoutSpacing.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
