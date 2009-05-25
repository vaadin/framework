package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class ComboBoxPlain extends Feature {
    @Override
    public String getName() {
        return "Combobox";
    }

    @Override
    public String getDescription() {
        return "A drop-down selection component with single item selection."
                + " Shown here is the most basic variant, which basically"
                + " provides the same functionality as a NativeSelect with"
                + " added lazy-loading if there are many options.<br/>"
                + " See related examples for more advanced features.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { ComboBoxStartsWith.class, ComboBoxContains.class,
                ComboBoxNewItems.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
