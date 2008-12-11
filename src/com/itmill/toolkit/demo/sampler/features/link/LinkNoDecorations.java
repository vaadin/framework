package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonLink;
import com.itmill.toolkit.ui.Link;

public class LinkNoDecorations extends Feature {

    public String getDescription() {
        return "A link that opens a new window w/o decorations";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Link.class) };
    }

    
    public Class[] getRelatedFeatures() {
        return new Class[] { LinkCurrentWindow.class, LinkSizedWindow.class,
                ButtonLink.class };
    }

    
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
