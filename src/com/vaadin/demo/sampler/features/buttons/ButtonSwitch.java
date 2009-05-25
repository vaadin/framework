package com.vaadin.demo.sampler.features.buttons;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Button;

@SuppressWarnings("serial")
public class ButtonSwitch extends Feature {

    @Override
    public String getName() {
        return "Switch button";
    }

    @Override
    public String getDescription() {
        return "A switch button works like a regular push button, triggering"
                + " a server-side event, but it's state is 'sticky': the button"
                + " toggles between it's on and off states, instead of popping"
                + " right back out.<br/>Also know as a CheckBox.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonPush.class, ButtonLink.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
