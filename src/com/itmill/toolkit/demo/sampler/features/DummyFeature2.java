package com.itmill.toolkit.demo.sampler.features;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class DummyFeature2 extends Feature {

    public String getName() {
        return "A second placeholder feature";
    }

    public String getDescription() {
        return "A second description";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(String.class),
                new APIResource(PortletContext.class),
                new APIResource(ServletContext.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DummyFeature.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
        //
        new NamedExternalResource("CSS", getThemeBase() + "dummy/styles.css")
        //
        };
    }

    public Component getExample() {

        return new DummyFeatureExample();

    }

}
