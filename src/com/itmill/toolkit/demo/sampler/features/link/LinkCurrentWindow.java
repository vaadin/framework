package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonLink;
import com.itmill.toolkit.ui.Link;

public class LinkCurrentWindow extends Feature {

    public String getDescription() {
        return "An external link.";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Link.class) };
    }

    
    public Class[] getRelatedFeatures() {
        return new Class[] { LinkNoDecorations.class, LinkSizedWindow.class,
                ButtonLink.class };
    }

    
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
