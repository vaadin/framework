package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Label;

public class TextArea extends Feature {
    @Override
    public String getName() {
        return "Textarea";
    }

    @Override
    public String getDescription() {
        return "A multi-line Textfield component allows you to input"
                + " several lines of text."
                + "<br>The amount of columns and lines can be set, and both are set here to"
                + " 20 characters. Note that this only affects the width and height of the"
                + " component, not the allowed length of input.";
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
