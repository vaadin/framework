package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class ComboBoxNewItems extends Feature {
    @Override
    public String getName() {
        return "Combobox, enter new items";
    }

    @Override
    public String getDescription() {
        return "A drop-down selection component with single item selection.<br/>"
                + " This example also allows you to input your own"
                + " choice - your input will be added to the selection"
                + " of available choices. This behavior is built-in and can"
                + " be enabled with one method call. Note that by using this"
                + " feature, one can easily create <i>suggestion box</i> -type"
                + " inputs that for example remembers the users previous input,"
                + " or provides suggestions from a list of popular choices."
                + " Configured like this (and optionally with a filter), the"
                + " ComboBox can be a powerful alternative to TextField.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { ComboBoxPlain.class, ComboBoxStartsWith.class,
                ComboBoxContains.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
