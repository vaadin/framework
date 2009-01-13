package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxNewItems extends Feature {
    @Override
    public String getName() {
        return "Combobox - Add items";
    }

    @Override
    public String getDescription() {
        return "A drop-down selection component with single item selection."
                + " This selection box also allows you to input your own"
                + " choice. Your input will also be added to the selection"
                + " of available choices.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ComboBoxPlain.class, ComboBoxStartsWith.class,
                ComboBoxContains.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
