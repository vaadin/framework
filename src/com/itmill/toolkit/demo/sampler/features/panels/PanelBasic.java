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
        return "Panel with caption";
    }

    @Override
    public String getDescription() {
        return "Panel is a simple container that supports scrolling.<br/>"
                + " It's internal layout (by default VerticalLayout) can be"
                + " configured or exchanged to get desired results. Components"
                + " that are added to the Panel will in effect be added to the"
                + " layout.";

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
