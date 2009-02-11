package com.itmill.toolkit.demo.sampler.features.panels;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Panel;

public class PanelLight extends Feature {
    @Override
    public String getName() {
        return "Panel, light style";
    }

    @Override
    public String getDescription() {
        return "The 'light' style version has less decorations than the regular Panel style.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Panel.class),
                new APIResource(Layout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { PanelBasic.class, FeatureSet.Layouts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
