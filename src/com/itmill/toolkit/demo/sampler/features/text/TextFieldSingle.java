package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TextField;

public class TextFieldSingle extends Feature {
    @Override
    public String getName() {
        return "Textfield";
    }

    @Override
    public String getDescription() {
        return "A single line Textfield component allows you to input"
                + " one line of text.<br/>"
                + "<br/>For sensitive data input, the textfield can"
                + " also be set into secret mode where the input will not be"
                + " echoed to display.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TextField.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { FeatureSet.Texts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
