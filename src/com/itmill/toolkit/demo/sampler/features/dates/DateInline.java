package com.itmill.toolkit.demo.sampler.features.dates;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.InlineDateField;

public class DateInline extends Feature {
    @Override
    public String getName() {
        return "Inline date selection";
    }

    @Override
    public String getDescription() {
        return "In this example, the resolution is set to be one day"
                + " and the DateField component is shown as an inline calendar"
                + " component.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(DateField.class),
                new APIResource(InlineDateField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DatePopup.class, DateLocale.class,
                DateResolution.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
