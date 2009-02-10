package com.itmill.toolkit.demo.sampler.features.accordions;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Accordion;

public class AccordionDisabled extends Feature {
    @Override
    public String getName() {
        return "Accordion, disabled tabs";
    }

    @Override
    public String getDescription() {
        return "An accordion component is a specialized case of a"
                + " tabsheet, with vertically organized tabs."
                + " With an accordion component you can also disable, enable,"
                + " hide and show tabs, similar to a tabsheet.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Accordion.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { AccordionIcons.class, FeatureSet.Tabsheets.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
