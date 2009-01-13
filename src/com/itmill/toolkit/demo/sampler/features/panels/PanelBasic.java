package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Panel;

public class PanelBasic extends Feature {
    @Override
    public String getName() {
        return "Panel with caption";
    }

    @Override
    public String getDescription() {
        return "Panel is a simple container for one component."
                + " A caption can optionally be added."
                + "<br>A panel typically contains a layout component"
                + " where the other components can be added.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Panel.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { PanelLight.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
