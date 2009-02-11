package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Panel;

public class PanelBasic extends Feature {
    @Override
    public String getName() {
        return "Panel";
    }

    @Override
    public String getDescription() {
        return "";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Panel.class),
                new APIResource(Layout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { PanelLight.class, FeatureSet.Layouts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
