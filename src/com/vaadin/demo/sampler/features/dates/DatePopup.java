package com.vaadin.demo.sampler.features.dates;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.DateField;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class DatePopup extends Feature {
    @Override
    public String getName() {
        return "Pop-up date selection";
    }

    @Override
    public String getDescription() {
        return "In this example, the resolution is set to be one day"
                + " and the DateField component is shown as a calendar pop-up.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(DateField.class),
                new APIResource(PopupDateField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DateInline.class, DateLocale.class,
                DateResolution.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
