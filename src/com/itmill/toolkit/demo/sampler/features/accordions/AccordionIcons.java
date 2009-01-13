package com.itmill.toolkit.demo.sampler.features.accordions;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Accordion;

public class AccordionIcons extends Feature {
    @Override
    public String getName() {
        return "Accordion with icons";
    }

    @Override
    public String getDescription() {
        return "An accordion component is a specialized case of a"
                + " tabsheet. Within an accordion, the tabs are organized"
                + " vertically, and the content will be shown directly"
                + " below the tab.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Accordion.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { AccordionDisabled.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
