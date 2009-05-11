package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.selects.ComboBoxNewItems;
import com.vaadin.ui.TextField;

public class TextFieldSecret extends Feature {
    @Override
    public String getName() {
        return "Text field, secret (password)";
    }

    @Override
    public String getDescription() {
        return "For sensitive data input, such as passwords, the text field can"
                + " also be set into secret mode where the input will not be"
                + " echoed to display.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TextField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        // TODO update CB -ref to 'suggest' pattern, when available
        return new Class[] { TextFieldSingle.class, ComboBoxNewItems.class,
                FeatureSet.Texts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
