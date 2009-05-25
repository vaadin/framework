package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LabelRich extends Feature {
    @Override
    public String getName() {
        return "Label, rich text";
    }

    @Override
    public String getDescription() {
        return "In this example the content mode is set to"
                + " CONTENT_XHTML. This content mode assumes that the"
                + " content set to the label will be valid XHTML.<br/>"
                + "Click the <i>Edit</i> button to edit the label content.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Label.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LabelPlain.class, LabelPreformatted.class,
                RichTextEditor.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
