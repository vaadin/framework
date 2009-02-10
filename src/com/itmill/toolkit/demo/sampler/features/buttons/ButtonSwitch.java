package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Button;

public class ButtonSwitch extends Feature {

    @Override
    public String getName() {
        return "Switch button";
    }

    @Override
    public String getDescription() {
        return "A switch button works like a regular button, triggering a server-side event, but it's state is 'sticky' - it toggles on or off, instead of popping right back out.<br/>Also know as a CheckBox.";
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
