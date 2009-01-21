package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.SplitPanel;

public class SplitPanelBasic extends Feature {

    @Override
    public String getName() {
        return "SplitPanel";
    }

    @Override
    public String getDescription() {
        return "SplitPanel is a component container that can contain two components."
                + " The split orientation can be either horizontal or vertical. Various"
                + " settings regarding eg. region sizes and resizeability can be set."
                + "<br>Click the button for a demo in a new window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(SplitPanel.class),

        };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {};
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
