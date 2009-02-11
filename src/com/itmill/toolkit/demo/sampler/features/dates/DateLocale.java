package com.itmill.toolkit.demo.sampler.features.dates;

import java.util.Locale;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.InlineDateField;

public class DateLocale extends Feature {
    @Override
    public String getName() {
        return "Date selection, locale";
    }

    @Override
    public String getDescription() {
        return "In this example, you can select a different locale"
                + " from the combo box and see how the calendar component"
                + " will be localized.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(DateField.class),
                new APIResource(InlineDateField.class),
                new APIResource(Locale.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { DateInline.class, DatePopup.class,
                DateResolution.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
