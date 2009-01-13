package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxContains extends Feature {
    @Override
    public String getName() {
        return "Combobox - Contains";
    }

    @Override
    public String getDescription() {
        return "A drop-down selection component with single item selection."
                + " A 'contains' filter has been used with this combo box,"
                + " so you can key in some text and only the options"
                + " containing your input will be shown.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ComboBoxPlain.class, ComboBoxStartsWith.class,
                ComboBoxNewItems.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
