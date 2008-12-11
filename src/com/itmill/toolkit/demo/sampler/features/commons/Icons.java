package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class Icons extends Feature {

    
    public String getDescription() {
        return "Most components can have an <i>icon</i>,"
                + " which is usually displayed next to the caption.<br/>"
                + "When used correctly, icons can make it significantly"
                + " easier for the user to find a specific functionality."
                + " Beware of overuse, which will have the opposite effect.";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Component.class) };
    }

    
    public Class[] getRelatedFeatures() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
