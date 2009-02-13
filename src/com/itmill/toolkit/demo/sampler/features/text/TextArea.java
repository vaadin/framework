package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TextField;

public class TextArea extends Feature {
    @Override
    public String getName() {
        return "Text area";
    }

    @Override
    public String getDescription() {
        return "A text field can be configured to allow multiple lines of input."
                + "<br>The amount of columns and lines can be set, and both are set here to"
                + " 20 characters. Note that this only affects the width and height of the"
                + " component, not the allowed length of input.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TextField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { RichTextEditor.class, TextFieldSingle.class,
                FeatureSet.Texts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
