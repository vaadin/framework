package com.vaadin.demo.sampler.features.commons;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;

public class Icons extends Feature {

    @Override
    public String getName() {
        return "Icons";
    }

    @Override
    public String getDescription() {
        return "Most components can have an <i>icon</i>,"
                + " which is usually displayed next to the caption.<br/>"
                + "When used correctly, icons can make it significantly"
                + " easier for the user to find a specific functionality."
                + " Beware of overuse, which will have the opposite effect.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Component.class),
                new APIResource(Resource.class),
                new APIResource(ApplicationResource.class),
                new APIResource(ClassResource.class),
                new APIResource(ExternalResource.class),
                new APIResource(FileResource.class),
                new APIResource(StreamResource.class),
                new APIResource(ThemeResource.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        // TODO link embedded sample
        return null;
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
