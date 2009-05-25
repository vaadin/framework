package com.vaadin.demo.sampler.features.buttons;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.vaadin.ui.Button;

@SuppressWarnings("serial")
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
