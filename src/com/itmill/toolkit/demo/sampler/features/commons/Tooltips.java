package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.AbstractComponent;

public class Tooltips extends Feature {

    
    public String getDescription() {
        return "Most components can have a <i>description</i>,"
                + " which is shown as a <i>\"tooltip\"</i>.<br/>"
                + "Descriptions may have rich content.<br/>"
                + "Note that <i>description</i> is more generic term than"
                + " <i>tooltip</i> - a component might choose to show"
                + " the description in another way, if that's more appropriate"
                + " for that compoenent.)";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(AbstractComponent.class) };
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
