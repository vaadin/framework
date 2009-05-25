package com.vaadin.demo.sampler.features.link;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.buttons.ButtonLink;
import com.vaadin.ui.Link;

@SuppressWarnings("serial")
public class LinkCurrentWindow extends Feature {

    @Override
    public String getName() {
        return "Link";
    }

    @Override
    public String getDescription() {
        return "By default, links open in the current browser window (use the browser back-button to get back).";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Link.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LinkNoDecorations.class, LinkSizedWindow.class,
                ButtonLink.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
