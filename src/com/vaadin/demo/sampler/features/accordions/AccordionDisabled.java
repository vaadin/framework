package com.vaadin.demo.sampler.features.accordions;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Accordion;

@SuppressWarnings("serial")
public class AccordionDisabled extends Feature {
    @Override
    public String getName() {
        return "Accordion, disabled tabs";
    }

    @Override
    public String getDescription() {
        return "You can disable, enable, hide and show accordion 'tabs'.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Accordion.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { AccordionIcons.class, FeatureSet.Tabsheets.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
