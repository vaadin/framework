package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.SplitPanel;

public class SplitPanelBasic extends Feature {

    @Override
    public String getName() {
        return "Split panel";
    }

    @Override
    public String getDescription() {
        return "The SplitPanel has two resizable component areas, either"
                + " vertically or horizontally oriented. The split position"
                + " can optionally be locked.<br/> By nesting split panels,"
                + " one can make quite complicated, dynamic layouts.";
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
