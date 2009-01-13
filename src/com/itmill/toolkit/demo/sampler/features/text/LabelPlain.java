package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Label;

public class LabelPlain extends Feature {
    @Override
    public String getName() {
        return "Label - Plain text";
    }

    @Override
    public String getDescription() {
        return "A label is a simple component that allows you to add"
                + " optionally formatted text components to your"
                + " application."
                + "<br>In this example the content mode is set to"
                + " CONTENT_TEXT, meaning that the label will contain"
                + " only plain text.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Label.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { LabelPreformatted.class, LabelRich.class,
                TextFieldSingle.class, TextArea.class, RichTextEditor.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
