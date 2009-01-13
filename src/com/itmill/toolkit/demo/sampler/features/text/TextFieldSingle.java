package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Label;

public class TextFieldSingle extends Feature {
    @Override
    public String getName() {
        return "Textfield";
    }

    @Override
    public String getDescription() {
        return "A single line Textfield component allows you to input"
                + " one line of text."
                + "<br>In this example the text will be shown in a"
                + " label component after you press enter."
                + "<br>The amount of columns can be set, and is set here to"
                + " 5 characters. Note that this only affects the width of the"
                + " component, not the allowed length of input."
                + "<br>For sensitive data input, the textfield can"
                + " also be set into secret mode where the input will not be"
                + " echoed to display.";
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
