package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Label;

public class LabelRich extends Feature {
    @Override
    public String getName() {
        return "Label, rich text";
    }

    @Override
    public String getDescription() {
        return "A label is a simple component that allows you to add"
                + " optionally formatted text components to your"
                + " application."
                + "<br>In this example the content mode is set to"
                + " CONTENT_XHTML. This content mode assumes that the"
                + " content set to the label will be valid XHTML.";
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
