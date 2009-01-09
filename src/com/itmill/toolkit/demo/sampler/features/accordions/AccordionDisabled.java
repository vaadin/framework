package com.itmill.toolkit.demo.sampler.features.accordions;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Accordion;

public class AccordionDisabled extends Feature {

    @Override
    public String getDescription() {
        return "With an accordion component you can disable, enable,"
                + " hide and show tabs, similary to a tabsheet.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Accordion.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { AccordionIcons.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
