package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LabelPreformatted extends Feature {
    @Override
    public String getName() {
        return "Label, preformatted";
    }

    @Override
    public String getDescription() {
        return "In this example the content mode is set to"
                + " CONTENT_PREFORMATTED. The text for this content type"
                + " is by default rendered with fixed-width font. Line breaks"
                + " can be inserted with \\n and tabulator characters with \\t.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Label.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LabelPlain.class, LabelRich.class,
                TextFieldSingle.class, TextArea.class, RichTextEditor.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
