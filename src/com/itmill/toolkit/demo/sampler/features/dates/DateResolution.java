package com.itmill.toolkit.demo.sampler.features.dates;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.InlineDateField;

public class DateResolution extends Feature {
    @Override
    public String getName() {
        return "Date selection - Resolution";
    }

    @Override
    public String getDescription() {
        return "The DateField component can be used to produce various"
                + " date and time input fields with different resolutions."
                + " The date and time format used with this component is"
                + " reported to the Toolkit by the browser."
                + "<br>In this example, you can select a different resolution"
                + " from the combo box and see how the calendar component"
                + " will change.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(DateField.class),
                new APIResource(InlineDateField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DateInline.class, DatePopup.class,
                DateLocale.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
