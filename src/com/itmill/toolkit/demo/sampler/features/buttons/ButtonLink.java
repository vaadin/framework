package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.demo.sampler.features.link.LinkCurrentWindow;
import com.itmill.toolkit.ui.Button;

public class ButtonLink extends Feature {

    @Override
    public String getName() {
        return "Link button";
    }

    @Override
    public String getDescription() {
        return "A link-styled button works like a push button, but looks like"
                + " a Link.<br/> It does not actually link somewhere, but"
                + " triggers a server-side event, just like a regular button.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonPush.class, ButtonSwitch.class,
                LinkCurrentWindow.class, ProminentPrimaryAction.class,
                FeatureSet.Links.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
