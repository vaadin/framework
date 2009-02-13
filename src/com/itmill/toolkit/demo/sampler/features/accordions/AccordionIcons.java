package com.itmill.toolkit.demo.sampler.features.accordions;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Accordion;

public class AccordionIcons extends Feature {
    @Override
    public String getName() {
        return "Accordion with icons";
    }

    @Override
    public String getDescription() {
        return "The accordion 'tabs' can contain icons in addition to the caption.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Accordion.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { AccordionDisabled.class,
                FeatureSet.Tabsheets.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
