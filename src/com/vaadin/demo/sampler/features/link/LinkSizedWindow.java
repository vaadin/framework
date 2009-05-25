package com.vaadin.demo.sampler.features.link;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.buttons.ButtonLink;
import com.vaadin.ui.Link;

@SuppressWarnings("serial")
public class LinkSizedWindow extends Feature {

    @Override
    public String getName() {
        return "Link, sized window";
    }

    @Override
    public String getDescription() {
        return "Links can configure the size of the opened window.<br/>These links open a small fixed size window without decorations.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Link.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LinkCurrentWindow.class, LinkNoDecorations.class,
                ButtonLink.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
