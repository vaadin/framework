package com.itmill.toolkit.demo.sampler.features;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Panel;

public class DummyFeature extends Feature {

    public String getName() {
        return "A placeholder feature";
    }

    public String getDescription() {
        return "A description";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Panel.class),
                new APIResource(Button.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DummyFeature2.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
        //
        new NamedExternalResource("CSS", getThemeBase() + "dummy/styles.css")
        //
        };
    }

}
