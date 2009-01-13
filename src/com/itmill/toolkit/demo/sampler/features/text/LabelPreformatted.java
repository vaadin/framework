package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Label;

public class LabelPreformatted extends Feature {
    @Override
    public String getName() {
        return "Label - Preformatted";
    }

    @Override
    public String getDescription() {
        return "A label is a simple component that allows you to add"
                + " optionally formatted text components to your"
                + " application."
                + "<br>In this example the content mode is set to"
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
