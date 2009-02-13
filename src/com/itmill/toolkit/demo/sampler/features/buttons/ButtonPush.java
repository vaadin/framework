package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.ui.Button;

public class ButtonPush extends Feature {

    @Override
    public String getName() {
        return "Push button";
    }

    @Override
    public String getDescription() {
        return "A push-button, which can be considered a 'regular' button,"
                + " returns to it's 'unclicked' state after emitting an event"
                + " when the user clicks it.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonLink.class, ButtonSwitch.class,
                ProminentPrimaryAction.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
