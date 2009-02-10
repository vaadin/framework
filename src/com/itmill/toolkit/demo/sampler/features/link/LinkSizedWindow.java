package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonLink;
import com.itmill.toolkit.ui.Link;

public class LinkSizedWindow extends Feature {

    @Override
    public String getName() {
        return "Link, sized window";
    }

    @Override
    public String getDescription() {
        return "An basic HTML-style (external) link, opening a small window w/o"
                + " decorations.<br/> A link changes"
                + " the url of the browser w/o triggering a server-side event"
                + " (like the link-styled Button).<br/> Links can open new"
                + " browser windows, and configure the amount of browser"
                + " features shown, such as toolbar and addressbar, and can also"
                + " set the size of the window.";
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
