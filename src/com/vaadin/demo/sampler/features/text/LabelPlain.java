package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LabelPlain extends Feature {
    @Override
    public String getName() {
        return "Label, plain text";
    }

    @Override
    public String getDescription() {
        return "In this example the content mode is set to"
                + " CONTENT_TEXT, meaning that the label will contain"
                + " only plain text.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Label.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { LabelPreformatted.class, LabelRich.class,
                TextFieldSingle.class, TextArea.class, RichTextEditor.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
