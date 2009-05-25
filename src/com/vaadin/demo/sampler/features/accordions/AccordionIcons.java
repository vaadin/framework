package com.vaadin.demo.sampler.features.accordions;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Accordion;

@SuppressWarnings("serial")
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { AccordionDisabled.class,
                FeatureSet.Tabsheets.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
