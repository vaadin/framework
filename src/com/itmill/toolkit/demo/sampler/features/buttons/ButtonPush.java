package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.ui.Button;

public class ButtonPush extends Feature {

    public String getDescription() {
        return "A basic push-button.";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class) };
    }

    
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonLink.class, ButtonSwitch.class,
                ProminentPrimaryAction.class };
    }

    
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
