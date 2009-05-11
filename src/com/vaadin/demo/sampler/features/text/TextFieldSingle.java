package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.selects.ComboBoxNewItems;
import com.vaadin.ui.TextField;

public class TextFieldSingle extends Feature {
    @Override
    public String getName() {
        return "Text field";
    }

    @Override
    public String getDescription() {
        return "A single-line TextField is a fundamental UI building blocks"
                + " with numerous uses.<br/>"
                + "If the input would benefit from remembering previous values,"
                + " you might want to consider using a ComboBox it it's "
                + " 'suggesting mode' instead.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TextField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        // TODO update CB -ref to 'suggest' pattern, when available
        return new Class[] { TextFieldSecret.class, ComboBoxNewItems.class,
                FeatureSet.Texts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
