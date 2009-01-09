package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Panel;

public class PanelLight extends Feature {

    @Override
    public String getDescription() {
        return "A lighter style for the Panel component is"
                + " also available.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Panel.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { PanelBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
