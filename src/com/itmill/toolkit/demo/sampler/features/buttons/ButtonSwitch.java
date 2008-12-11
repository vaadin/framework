package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.ui.Button;

public class ButtonSwitch extends Feature {

    public String getDescription() {
        return "A switch button.";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class) };
    }

    
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonPush.class, ButtonLink.class,
                ProminentPrimaryAction.class };
    }

    
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
